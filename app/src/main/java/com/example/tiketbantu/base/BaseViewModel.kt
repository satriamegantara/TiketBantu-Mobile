package com.example.tiketbantu.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Base class for all ViewModels in TiketBantu Mobile.
 * Encapsulates coroutine execution with safe exception handling.
 */
open class BaseViewModel : ViewModel() {

    private val defaultExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    /**
     * Override in subclasses to handle unhandled exceptions from coroutines.
     */
    protected open fun handleError(throwable: Throwable) {
        // Default: no-op or log error
    }

    /**
     * Launches a coroutine safely in [viewModelScope] using the [defaultExceptionHandler].
     */
    protected fun launchSafe(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(defaultExceptionHandler) {
            block()
        }
}
