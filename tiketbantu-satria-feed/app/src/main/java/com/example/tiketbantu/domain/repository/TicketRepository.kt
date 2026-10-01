package com.example.tiketbantu.domain.repository

import com.example.tiketbantu.domain.model.Ticket
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract for Ticket operations.
 *
 * CHANGED (Anggota 3): [getAllTickets] and [getTicketById] now take [currentUserId] so that
 * Ticket.isSupportedByMe can be computed, and [getAllTickets] takes [limit] for infinite scroll.
 * All new parameters have defaults, so existing callers keep compiling.
 *
 * Ordering contract of [getAllTickets]: SELESAI/DITUTUP tickets are always returned after
 * active ones; within each block order is most-supported-first when [sortByMostLiked],
 * otherwise newest-first.
 */
interface TicketRepository {
    fun getAllTickets(
        query: String = "",
        categoryId: Long? = null,
        status: String? = null,
        sortByMostLiked: Boolean = false,
        currentUserId: Long = 0L,
        limit: Int = Int.MAX_VALUE
    ): Flow<List<Ticket>>

    fun getTicketById(id: Long, currentUserId: Long = 0L): Flow<Ticket?>
    fun getMyTickets(userId: Long): Flow<List<Ticket>>
    suspend fun createTicket(ticket: Ticket): Long
    suspend fun updateTicketStatus(ticketId: Long, status: String, agentId: Long? = null)

    /** @return true if the user now supports the ticket, false if the support was removed. */
    suspend fun toggleSupport(ticketId: Long, userId: Long): Boolean
    suspend fun softDeleteTicket(ticketId: Long)
}
