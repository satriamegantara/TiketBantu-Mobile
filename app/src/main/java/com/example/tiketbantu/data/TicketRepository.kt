package com.example.tiketbantu.data

import com.example.tiketbantu.domain.model.Ticket
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for Ticket data operations.
 */
interface TicketRepository {
    fun getAll(): Flow<List<Ticket>>
    suspend fun add(ticket: Ticket)
    suspend fun toggleLike(ticketId: String, userId: String)
}
