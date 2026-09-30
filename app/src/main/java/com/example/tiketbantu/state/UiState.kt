package com.example.tiketbantu.state

/**
 * Standard UI state wrapper used across ViewModels.
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val throwable: Throwable) : UiState<Nothing>()
}
