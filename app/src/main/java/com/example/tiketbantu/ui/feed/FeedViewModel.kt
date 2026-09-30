package com.example.tiketbantu.ui.feed

import androidx.lifecycle.viewModelScope
import com.example.tiketbantu.base.BaseViewModel
import com.example.tiketbantu.domain.usecase.GetFeedUseCase
import com.example.tiketbantu.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach

/**
 * ViewModel for the Feed screen. Exposes a [StateFlow] of [UiState] containing the list of tickets.
 */
class FeedViewModel(
    private val getFeedUseCase: GetFeedUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<com.example.tiketbantu.domain.model.Ticket>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<com.example.tiketbantu.domain.model.Ticket>>> = _uiState

    init {
        loadFeed()
    }

    private fun loadFeed() {
        launchSafe {
            getFeedUseCase()
                .onEach { tickets ->
                    _uiState.value = UiState.Success(tickets)
                }
                .catch { e ->
                    _uiState.value = UiState.Error(e)
                }
                .collect()
        }
    }
}
