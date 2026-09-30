package com.example.tiketbantu.data

import com.example.tiketbantu.domain.model.Ticket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Simple in‑memory repository used for development and UI preview.
 * It stores tickets in a MutableStateFlow so ViewModels can observe changes.
 */
class FakeTicketRepository : TicketRepository {
    private val tickets = MutableStateFlow<List<Ticket>>(emptyList())

    override fun getAll(): Flow<List<Ticket>> = tickets.asStateFlow()

    override suspend fun add(ticket: Ticket) {
        tickets.value = tickets.value + ticket
    }

    override suspend fun toggleLike(ticketId: String, userId: String) {
        tickets.value = tickets.value.map { ticket ->
            if (ticket.id == ticketId) {
                val newLikes = if (userId in ticket.likedBy) {
                    ticket.likedBy - userId
                } else {
                    ticket.likedBy + userId
                }
                ticket.copy(likedBy = newLikes)
            } else {
                ticket
            }
        }
    }
}
