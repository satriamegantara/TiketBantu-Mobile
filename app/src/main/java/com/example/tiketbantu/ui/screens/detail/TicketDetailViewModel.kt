package com.example.tiketbantu.ui.screens.detail

import androidx.lifecycle.viewModelScope
import com.example.tiketbantu.base.BaseViewModel
import com.example.tiketbantu.base.UiState
import com.example.tiketbantu.domain.model.Comment
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.domain.model.TicketStatusRules
import com.example.tiketbantu.domain.model.User
import com.example.tiketbantu.domain.repository.AuthRepository
import com.example.tiketbantu.domain.repository.CommentRepository
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.domain.usecase.ToggleSupportUseCase
import com.example.tiketbantu.domain.usecase.UpdateTicketStatusUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

/** One-shot UI events of the detail screen. */
sealed interface DetailEvent {
    data class ShowMessage(val message: String) : DetailEvent

    /** Emitted after the comment list already contains the new comment (screen scrolls to it). */
    data object CommentSent : DetailEvent
}

/**
 * ViewModel for the ticket detail screen:
 *  - 3.4 ticket info, kept live through the repository Flow (support count updates instantly);
 *  - 3.5 comment thread: loaded on open, refreshed after posting and by the screen's 5s polling;
 *  - 3.6 status update (Agen only), validated by [TicketStatusRules].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TicketDetailViewModel(
    private val ticketId: Long,
    private val ticketRepository: TicketRepository,
    private val commentRepository: CommentRepository,
    private val authRepository: AuthRepository,
    private val toggleSupportUseCase: ToggleSupportUseCase,
    private val updateTicketStatusUseCase: UpdateTicketStatusUseCase
) : BaseViewModel() {

    private val _ticketState = MutableStateFlow<UiState<Ticket>>(UiState.Loading)
    val ticketState: StateFlow<UiState<Ticket>> = _ticketState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _commentInput = MutableStateFlow("")
    val commentInput: StateFlow<String> = _commentInput.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    /** True only for the assigned Agen while the ticket is DIPROSES (drives the status buttons). */
    val canUpdateStatus: StateFlow<Boolean> =
        combine(_ticketState, _currentUser) { state, user ->
            state is UiState.Success && user != null && TicketStatusRules.canUpdate(user, state.data)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _events = Channel<DetailEvent>(Channel.BUFFERED)
    val events: Flow<DetailEvent> = _events.receiveAsFlow()

    private var ticketJob: Job? = null

    init {
        observeTicket()
        refreshComments()
    }

    private fun observeTicket() {
        ticketJob?.cancel()
        ticketJob = authRepository.getCurrentUser()
            .flatMapLatest { user ->
                ticketRepository.getTicketById(ticketId, user?.id ?: 0L).map { ticket -> user to ticket }
            }
            .onEach { (user, ticket) ->
                _currentUser.value = user
                _ticketState.value =
                    if (ticket == null) {
                        UiState.Error("Aduan tidak ditemukan atau sudah dihapus.")
                    } else {
                        UiState.Success(ticket)
                    }
            }
            .catch { e ->
                _ticketState.value = UiState.Error(
                    message = e.localizedMessage ?: "Gagal memuat detail aduan.",
                    throwable = e
                )
            }
            .launchIn(viewModelScope)
    }

    fun retry() {
        _ticketState.value = UiState.Loading
        observeTicket()
        refreshComments()
    }

    // ---- 3.5 comments + polling ----

    /**
     * Reloads the thread. Called by the screen every 5 seconds (APP_FLOW alur 7), so failures are
     * swallowed here instead of showing a Snackbar every tick.
     */
    fun refreshComments() {
        launchSafe {
            try {
                _comments.value = commentRepository.getComments(ticketId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Keep the last known list; the next tick retries.
            }
        }
    }

    fun onCommentInputChange(text: String) {
        _commentInput.value = text.take(MAX_COMMENT_LENGTH)
    }

    fun sendComment() {
        val content = _commentInput.value.trim()
        if (content.isEmpty() || _isSending.value) return

        launchSafe {
            _isSending.value = true
            try {
                val user = authRepository.getCurrentUser().first()
                if (user == null || !user.isActive) {
                    send(DetailEvent.ShowMessage("Silakan masuk terlebih dahulu."))
                    return@launchSafe
                }
                commentRepository.addComment(ticketId, user.id, content)
                _commentInput.value = ""
                _comments.value = commentRepository.getComments(ticketId)
                send(DetailEvent.ShowMessage("Komentar terkirim."))
                send(DetailEvent.CommentSent)
            } finally {
                _isSending.value = false
            }
        }
    }

    // ---- 3.3 support (also available on the detail page) ----

    fun onToggleSupport() {
        launchSafe {
            val user = authRepository.getCurrentUser().first()
            if (user == null || !user.isActive) {
                send(DetailEvent.ShowMessage("Silakan masuk terlebih dahulu."))
                return@launchSafe
            }
            val nowSupported = toggleSupportUseCase(ticketId, user.id)
            send(DetailEvent.ShowMessage(if (nowSupported) "Dukungan ditambahkan." else "Dukungan dibatalkan."))
        }
    }

    // ---- 3.6 linear status update (Agen) ----

    fun updateStatus(newStatus: String) {
        launchSafe {
            val user = authRepository.getCurrentUser().first()
            if (user == null) {
                send(DetailEvent.ShowMessage("Silakan masuk terlebih dahulu."))
                return@launchSafe
            }
            updateTicketStatusUseCase(ticketId, newStatus, user)
                .onSuccess {
                    val label = if (newStatus == TicketStatus.SELESAI) "Selesai" else "Ditutup"
                    send(DetailEvent.ShowMessage("Status aduan diperbarui menjadi $label."))
                }
                .onFailure { e ->
                    send(DetailEvent.ShowMessage(e.localizedMessage ?: "Gagal memperbarui status."))
                }
        }
    }

    private fun send(event: DetailEvent) {
        _events.trySend(event)
    }

    override fun handleError(throwable: Throwable) {
        send(DetailEvent.ShowMessage(throwable.localizedMessage ?: "Terjadi kesalahan, coba lagi."))
    }

    companion object {
        const val MAX_COMMENT_LENGTH = 500
    }
}
