package com.example.tiketbantu.ui.screens.feed

import androidx.lifecycle.viewModelScope
import com.example.tiketbantu.base.BaseViewModel
import com.example.tiketbantu.base.UiState
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.repository.AuthRepository
import com.example.tiketbantu.domain.usecase.GetFeedUseCase
import com.example.tiketbantu.domain.usecase.ToggleSupportUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

enum class FeedSort { LATEST, MOST_LIKED }

/**
 * User-selected filters. [sort] == null means "use the role default"
 * (Pelapor/Admin: LATEST, Agen: MOST_LIKED).
 */
data class FeedFilter(
    val status: String? = null,
    val categoryId: Long? = null,
    val sort: FeedSort? = null
)

/** One-shot UI events (shown as Snackbar). */
sealed interface FeedEvent {
    data class ShowMessage(val message: String) : FeedEvent
}

/**
 * ViewModel for the public complaint feed (task 3.2) and the "Saya Juga Mengalami" toggle (3.3).
 *
 * The feed is a single reactive pipeline: (current user, search text, filter, page limit, refresh tick)
 * -> flatMapLatest -> Room Flow. Because the repository returns a Flow, a support toggle or a
 * status change re-sorts the list instantly without any manual reload.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class FeedViewModel(
    private val getFeedUseCase: GetFeedUseCase,
    private val toggleSupportUseCase: ToggleSupportUseCase,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _feedState = MutableStateFlow<UiState<List<Ticket>>>(UiState.Loading)
    val feedState: StateFlow<UiState<List<Ticket>>> = _feedState.asStateFlow()

    private val _searchInput = MutableStateFlow("")
    val searchInput: StateFlow<String> = _searchInput.asStateFlow()

    private val _filter = MutableStateFlow(FeedFilter())
    val filter: StateFlow<FeedFilter> = _filter.asStateFlow()

    /** Sort actually applied (explicit choice, otherwise role default). Used to highlight the chip. */
    private val _effectiveSort = MutableStateFlow(FeedSort.LATEST)
    val effectiveSort: StateFlow<FeedSort> = _effectiveSort.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _canLoadMore = MutableStateFlow(false)
    val canLoadMore: StateFlow<Boolean> = _canLoadMore.asStateFlow()

    private val limit = MutableStateFlow(PAGE_SIZE)
    private val refreshTick = MutableStateFlow(0)

    private val _events = Channel<FeedEvent>(Channel.BUFFERED)
    val events: Flow<FeedEvent> = _events.receiveAsFlow()

    private var feedJob: Job? = null

    init {
        observeFeed()
    }

    private data class FeedParams(
        val userId: Long,
        val role: String?,
        val query: String,
        val filter: FeedFilter,
        val limit: Int,
        val tick: Int
    )

    private data class FeedResult(
        val params: FeedParams,
        val sort: FeedSort,
        val tickets: List<Ticket>
    )

    private fun observeFeed() {
        feedJob?.cancel()

        // Empty text applies immediately (clearing the field feels instant); typing is debounced.
        val queryFlow = _searchInput
            .debounce { text -> if (text.isEmpty()) 0L else SEARCH_DEBOUNCE_MS }
            .distinctUntilChanged()

        feedJob = combine(
            authRepository.getCurrentUser(),
            queryFlow,
            _filter,
            limit,
            refreshTick
        ) { user, query, filter, pageLimit, tick ->
            FeedParams(
                userId = user?.id ?: 0L,
                role = user?.role,
                query = query.trim(),
                filter = filter,
                limit = pageLimit,
                tick = tick
            )
        }
            .distinctUntilChanged()
            .flatMapLatest { params ->
                val sort = params.filter.sort ?: defaultSortFor(params.role)
                getFeedUseCase(
                    query = params.query,
                    categoryId = params.filter.categoryId,
                    status = params.filter.status,
                    sortByMostLiked = sort == FeedSort.MOST_LIKED,
                    currentUserId = params.userId,
                    limit = params.limit
                ).map { tickets -> FeedResult(params, sort, tickets) }
            }
            .onEach { result ->
                _effectiveSort.value = result.sort
                // A full page means there may be more rows behind it.
                _canLoadMore.value = result.tickets.size >= result.params.limit
                _feedState.value = UiState.Success(result.tickets)
                _isRefreshing.value = false
            }
            .catch { e ->
                _isRefreshing.value = false
                _canLoadMore.value = false
                _feedState.value = UiState.Error(
                    message = e.localizedMessage ?: "Gagal memuat feed aduan",
                    throwable = e
                )
            }
            .launchIn(viewModelScope)
    }

    private fun defaultSortFor(role: String?): FeedSort =
        if (role == ROLE_AGEN) FeedSort.MOST_LIKED else FeedSort.LATEST

    // ---- UI events ----

    fun onSearchChange(text: String) {
        _searchInput.value = text
        resetPaging()
    }

    fun onStatusSelected(status: String?) {
        _filter.update { it.copy(status = status) }
        resetPaging()
    }

    fun onSortSelected(sort: FeedSort) {
        _filter.update { it.copy(sort = sort) }
        resetPaging()
    }

    /** Category filter (feature 4.4). null = semua kategori. */
    fun onCategorySelected(categoryId: Long?) {
        _filter.update { it.copy(categoryId = categoryId) }
        resetPaging()
    }

    /** Pull-to-refresh. Room is already live; this re-runs the query from the first page. */
    fun refresh() {
        _isRefreshing.value = true
        limit.value = PAGE_SIZE
        refreshTick.update { it + 1 }
    }

    /** Infinite scroll: grow the page window. Ignored while a page is already loading. */
    fun loadMore() {
        if (!_canLoadMore.value) return
        _canLoadMore.value = false
        limit.update { it + PAGE_SIZE }
    }

    /** Restart the pipeline after an error state. */
    fun retry() {
        _feedState.value = UiState.Loading
        observeFeed()
    }

    /** "Saya Juga Mengalami": one tap toggles the current user's support. */
    fun onToggleSupport(ticketId: Long) {
        launchSafe {
            val user = authRepository.getCurrentUser().first()
            if (user == null || !user.isActive) {
                _events.trySend(FeedEvent.ShowMessage("Silakan masuk terlebih dahulu"))
                return@launchSafe
            }
            val nowSupported = toggleSupportUseCase(ticketId, user.id)
            _events.trySend(
                FeedEvent.ShowMessage(
                    if (nowSupported) "Dukungan ditambahkan" else "Dukungan dibatalkan"
                )
            )
        }
    }

    private fun resetPaging() {
        limit.value = PAGE_SIZE
    }

    override fun handleError(throwable: Throwable) {
        _events.trySend(
            FeedEvent.ShowMessage(throwable.localizedMessage ?: "Terjadi kesalahan, coba lagi")
        )
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val SEARCH_DEBOUNCE_MS = 300L
        const val ROLE_AGEN = "AGEN"
    }
}
