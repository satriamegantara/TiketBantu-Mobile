package com.example.tiketbantu.base

/**
 * Standard Unidirectional Data Flow (UDF) UI state wrapper for all ViewModels in TiketBantu.
 *
 * Usage pattern (in ViewModel):
 * ```kotlin
 * private val _uiState = MutableStateFlow<UiState<List<Ticket>>>(UiState.Idle)
 * val uiState: StateFlow<UiState<List<Ticket>>> = _uiState.asStateFlow()
 * ```
 *
 * Usage pattern (in Composable):
 * ```kotlin
 * val state by viewModel.uiState.collectAsStateWithLifecycle()
 * when (state) {
 *     is UiState.Idle    -> { /* show initial placeholder */ }
 *     is UiState.Loading -> { CircularProgressIndicator() }
 *     is UiState.Success -> { ContentList((state as UiState.Success).data) }
 *     is UiState.Error   -> { ErrorMessage((state as UiState.Error).message) }
 * }
 * ```
 *
 * @param T the type of data carried in [Success].
 */
sealed interface UiState<out T> {

    /** Initial state before any operation has been triggered. */
    data object Idle : UiState<Nothing>

    /** A background operation is in progress; show a loading indicator. */
    data object Loading : UiState<Nothing>

    /**
     * Operation completed successfully.
     * @param data the result payload.
     */
    data class Success<out T>(val data: T) : UiState<T>

    /**
     * Operation failed with a human-readable [message] and optional [throwable].
     * @param message localised / user-facing error description.
     * @param throwable the original exception (null if not applicable).
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null,
    ) : UiState<Nothing>
}

// ──────────────────────────────────────────────────────────────────────────────
// Extension helpers — keep business logic out of Composables
// ──────────────────────────────────────────────────────────────────────────────

/** Returns the wrapped data if this state is [UiState.Success], otherwise null. */
fun <T> UiState<T>.dataOrNull(): T? = (this as? UiState.Success)?.data

/** Returns true when this state is [UiState.Loading]. */
fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading

/** Returns true when this state is [UiState.Error]. */
fun <T> UiState<T>.isError(): Boolean = this is UiState.Error

/** Returns the error message if this state is [UiState.Error], otherwise null. */
fun <T> UiState<T>.errorMessageOrNull(): String? = (this as? UiState.Error)?.message
