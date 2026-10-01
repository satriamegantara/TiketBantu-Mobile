package com.example.tiketbantu.ui.screens.feed

import com.example.tiketbantu.base.BaseViewModel
import com.example.tiketbantu.base.UiState
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.usecase.GetFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart

/**
 * ViewModel for the Public Complaint Feed screen.
 */
class FeedViewModel(
    private val getFeedUseCase: GetFeedUseCase
) : BaseViewModel() {

    private val _feedState = MutableStateFlow<UiState<List<Ticket>>>(UiState.Loading)
    val feedState: StateFlow<UiState<List<Ticket>>> = _feedState.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed(
        query: String = "",
        categoryId: Long? = null,
        status: String? = null,
        sortByMostLiked: Boolean = false
    ) {
        launchSafe {
            getFeedUseCase(query, categoryId, status, sortByMostLiked)
                .onStart { _feedState.value = UiState.Loading }
                .catch { e ->
                    _feedState.value = UiState.Error(
                        message = e.localizedMessage ?: "Gagal memuat feed aduan",
                        throwable = e
                    )
                }
                .collect { tickets ->
                    _feedState.value = UiState.Success(tickets)
                }
        }
    }
}
