package com.example.tiketbantu.domain.repository

import com.example.tiketbantu.domain.model.Ticket
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract for Ticket operations.
 */
interface TicketRepository {
    fun getAllTickets(query: String = "", categoryId: Long? = null, status: String? = null, sortByMostLiked: Boolean = false): Flow<List<Ticket>>
    fun getTicketById(id: Long): Flow<Ticket?>
    fun getMyTickets(userId: Long): Flow<List<Ticket>>
    suspend fun createTicket(ticket: Ticket): Long
    suspend fun updateTicketStatus(ticketId: Long, status: String, agentId: Long? = null)
    suspend fun toggleSupport(ticketId: Long, userId: Long): Boolean
    suspend fun softDeleteTicket(ticketId: Long)
}
