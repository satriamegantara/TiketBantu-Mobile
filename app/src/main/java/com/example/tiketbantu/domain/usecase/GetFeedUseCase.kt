package com.example.tiketbantu.domain.usecase

import com.example.tiketbantu.data.TicketRepository
import com.example.tiketbantu.domain.model.Ticket
import kotlinx.coroutines.flow.Flow

/**
 * Use‑case that provides a stream of the current ticket feed.
 */
class GetFeedUseCase(private val repository: TicketRepository) {
    operator fun invoke(): Flow<List<Ticket>> = repository.getAll()
}
