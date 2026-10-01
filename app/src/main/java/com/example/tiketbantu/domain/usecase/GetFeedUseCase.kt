package com.example.tiketbantu.domain.usecase

import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase for retrieving the public complaint feed.
 */
class GetFeedUseCase(
    private val repository: TicketRepository
) {
    operator fun invoke(
        query: String = "",
        categoryId: Long? = null,
        status: String? = null,
        sortByMostLiked: Boolean = false,
        currentUserId: Long = 0L,
        limit: Int = Int.MAX_VALUE
    ): Flow<List<Ticket>> {
        return repository.getAllTickets(
            query = query,
            categoryId = categoryId,
            status = status,
            sortByMostLiked = sortByMostLiked,
            currentUserId = currentUserId,
            limit = limit
        )
    }
}
