package com.example.tiketbantu.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base class for **all** ViewModels in TiketBantu Mobile.
 *
 * Provides:
 * - [launchSafe] — coroutine launcher with automatic exception routing to [handleError].
 * - [executeWithState] — convenience wrapper that drives a [MutableStateFlow]<[UiState]<T>>
 *   through the Loading → Success / Error lifecycle automatically.
 * - [handleError] — override in subclasses to emit UI-facing error state or log.
 *
 * ### Typical subclass usage
 * ```kotlin
 * class FeedViewModel(private val getFeed: GetFeedUseCase) : BaseViewModel() {
 *
 *     private val _feedState = MutableStateFlow<UiState<List<Ticket>>>(UiState.Idle)
 *     val feedState: StateFlow<UiState<List<Ticket>>> = _feedState.asStateFlow()
 *
 *     fun loadFeed() {
 *         executeWithState(_feedState) { getFeed() }
 *     }
 * }
 * ```
 */
open class BaseViewModel : ViewModel() {

    // ── Private default exception handler ────────────────────────────────────

    private val defaultExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    // ── Protected helpers ─────────────────────────────────────────────────────

    /**
     * Override to handle uncaught coroutine exceptions (e.g., emit [UiState.Error]).
     * Default implementation is a no-op.
     */
    protected open fun handleError(throwable: Throwable) = Unit

    /**
     * Launches a coroutine safely in [viewModelScope] using [defaultExceptionHandler].
     * Prefer [executeWithState] for operations that emit [UiState].
     */
    protected fun launchSafe(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(defaultExceptionHandler) { block() }

    /**
     * Drives [stateFlow] through the full Loading → Success / Error lifecycle.
     *
     * Emits [UiState.Loading] before running [block], then [UiState.Success] with
     * the result on success, or [UiState.Error] on exception.
     *
     * @param stateFlow the [MutableStateFlow] to update.
     * @param block     suspend lambda that returns the data payload.
     */
    protected fun <T> executeWithState(
        stateFlow: MutableStateFlow<UiState<T>>,
        block: suspend () -> T,
    ) {
        viewModelScope.launch(defaultExceptionHandler) {
            stateFlow.value = UiState.Loading
            runCatching { block() }
                .onSuccess { data -> stateFlow.value = UiState.Success(data) }
                .onFailure { error ->
                    stateFlow.value = UiState.Error(
                        message = error.message ?: "Terjadi kesalahan yang tidak diketahui.",
                        throwable = error,
                    )
                    handleError(error)
                }
        }
    }

    /**
     * Convenience factory: returns a [StateFlow] backed by a new [MutableStateFlow]
     * initialised to [UiState.Idle].  Use the returned pair to expose the public
     * state and drive it via [executeWithState].
     *
     * ```kotlin
     * private val (_mutable, state) = stateFlowPair<List<Ticket>>()
     * val ticketState: StateFlow<UiState<List<Ticket>>> = state
     * ```
     */
    protected fun <T> stateFlowPair(): Pair<MutableStateFlow<UiState<T>>, StateFlow<UiState<T>>> {
        val mutable = MutableStateFlow<UiState<T>>(UiState.Idle)
        return mutable to mutable.asStateFlow()
    }
}
