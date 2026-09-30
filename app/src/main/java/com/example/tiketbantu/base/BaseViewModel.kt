package com.example.tiketbantu.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/**
 * Base class for all ViewModels in the project.
 * Provides a default [CoroutineExceptionHandler] that posts the exception to a
 * [handleError] method which can be overridden by subclasses.
 */
open class BaseViewModel : ViewModel() {
    private val defaultExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    /**
     * Called when a coroutine launched from the ViewModel throws an exception.
     * Sub‑classes can override to convert the exception into UI state, log it, etc.
     */
    protected open fun handleError(throwable: Throwable) {
        // Default implementation does nothing – keep it lightweight.
        // Subclasses should map the error to a UiState if needed.
    }

    /**
     * Convenience wrapper around [viewModelScope.launch] that automatically applies the
     * default exception handler.
     */
    protected fun launchSafe(block: suspend () -> Unit) =
        viewModelScope.launch(defaultExceptionHandler) { block() }
}
