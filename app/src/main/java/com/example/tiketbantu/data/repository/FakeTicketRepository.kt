package com.example.tiketbantu.data.repository

import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory fallback / testing repository implementing domain [TicketRepository].
 */
class FakeTicketRepository : TicketRepository {
    private val tickets = MutableStateFlow<List<Ticket>>(emptyList())

    override fun getAllTickets(
        query: String,
        categoryId: Long?,
        status: String?,
        sortByMostLiked: Boolean
    ): Flow<List<Ticket>> {
        return tickets.asStateFlow().map { list ->
            var result = list
            if (query.isNotBlank()) {
                result = result.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            }
            if (categoryId != null) {
                result = result.filter { it.categoryId == categoryId }
            }
            if (!status.isNullOrBlank()) {
                result = result.filter { it.status.equals(status, ignoreCase = true) }
            }
            if (sortByMostLiked) {
                result.sortedByDescending { it.supportCount }
            } else {
                result.sortedByDescending { it.createdAt }
            }
        }
    }

    override fun getTicketById(id: Long): Flow<Ticket?> {
        return tickets.asStateFlow().map { list -> list.find { it.id == id } }
    }

    override fun getMyTickets(userId: Long): Flow<List<Ticket>> {
        return tickets.asStateFlow().map { list -> list.filter { it.reporterId == userId } }
    }

    override suspend fun createTicket(ticket: Ticket): Long {
        val newId = (tickets.value.maxOfOrNull { it.id } ?: 0L) + 1L
        val newTicket = ticket.copy(id = newId)
        tickets.value = listOf(newTicket) + tickets.value
        return newId
    }

    override suspend fun updateTicketStatus(ticketId: Long, status: String, agentId: Long?) {
        tickets.value = tickets.value.map { ticket ->
            if (ticket.id == ticketId) {
                ticket.copy(
                    status = status,
                    agentId = agentId ?: ticket.agentId,
                    updatedAt = System.currentTimeMillis()
                )
            } else {
                ticket
            }
        }
    }

    override suspend fun toggleSupport(ticketId: Long, userId: Long): Boolean {
        var isNowSupported = false
        tickets.value = tickets.value.map { ticket ->
            if (ticket.id == ticketId) {
                val newCount = if (ticket.isSupportedByMe) ticket.supportCount - 1 else ticket.supportCount + 1
                isNowSupported = !ticket.isSupportedByMe
                ticket.copy(
                    isSupportedByMe = isNowSupported,
                    supportCount = maxOf(0, newCount)
                )
            } else {
                ticket
            }
        }
        return isNowSupported
    }

    override suspend fun softDeleteTicket(ticketId: Long) {
        tickets.value = tickets.value.filterNot { it.id == ticketId }
    }
}
