package com.example.tiketbantu.data.repository

import com.example.tiketbantu.data.local.dao.TicketDao
import com.example.tiketbantu.data.local.entity.TicketSupportEntity
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-backed implementation of [TicketRepository].
 * All reads are reactive ([Flow]) so the UI re-sorts instantly.
 */
class TicketRepositoryImpl(
    private val ticketDao: TicketDao
) : TicketRepository {

    override fun getAllTickets(
        query: String,
        categoryId: Long?,
        status: String?,
        sortByMostLiked: Boolean,
        currentUserId: Long,
        limit: Int
    ): Flow<List<Ticket>> =
        ticketDao.observeFeed(query, categoryId, status, sortByMostLiked, currentUserId, limit)
            .map { rows -> rows.map { it.toDomain() } }

    override fun getTicketById(id: Long, currentUserId: Long): Flow<Ticket?> =
        ticketDao.observeById(id, currentUserId).map { it?.toDomain() }

    override fun getMyTickets(userId: Long): Flow<List<Ticket>> =
        ticketDao.observeMine(userId).map { rows -> rows.map { it.toDomain() } }

    override fun getSupportedTickets(userId: Long): Flow<List<Ticket>> =
        ticketDao.observeSupported(userId).map { rows -> rows.map { it.toDomain() } }

    override suspend fun createTicket(ticket: Ticket): Long {
        val ticketId = ticketDao.insertTicket(ticket.toEntity())
        if (ticket.reporterId > 0L) {
            ticketDao.insertSupport(
                TicketSupportEntity(
                    ticketId = ticketId,
                    userId = ticket.reporterId,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        return ticketId
    }

    override suspend fun updateTicketStatus(ticketId: Long, status: String, agentId: Long?) {
        ticketDao.updateStatus(ticketId, status, agentId, System.currentTimeMillis())
    }

    override suspend fun updateTicketContent(
        ticketId: Long,
        title: String,
        description: String,
        categoryId: Long
    ) {
        ticketDao.updateTicketContent(ticketId, title, description, categoryId, System.currentTimeMillis())
    }

    override suspend fun toggleSupport(ticketId: Long, userId: Long): Boolean =
        ticketDao.toggleSupport(ticketId, userId)

    override suspend fun softDeleteTicket(ticketId: Long) {
        ticketDao.softDelete(ticketId, System.currentTimeMillis())
    }
}
