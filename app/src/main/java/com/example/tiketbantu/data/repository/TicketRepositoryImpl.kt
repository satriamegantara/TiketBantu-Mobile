package com.example.tiketbantu.data.repository

import com.example.tiketbantu.data.local.dao.SupportDao
import com.example.tiketbantu.data.local.dao.TicketDao
import com.example.tiketbantu.data.local.dao.TicketWithDetails
import com.example.tiketbantu.data.local.entity.TicketEntity
import com.example.tiketbantu.data.local.entity.TicketSupportEntity
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-backed implementation of [TicketRepository].
 * Single source of truth for all ticket operations.
 */
class TicketRepositoryImpl(
    private val ticketDao: TicketDao,
    private val supportDao: SupportDao
) : TicketRepository {

    override fun getAllTickets(
        query: String,
        categoryId: Long?,
        status: String?,
        sortByMostLiked: Boolean
    ): Flow<List<Ticket>> {
        return ticketDao.getFeedTickets(
            query = query,
            categoryId = categoryId,
            status = status,
            sortByMostLiked = if (sortByMostLiked) 1 else 0
        ).map { list -> list.map { it.toDomainModel() } }
    }

    override fun getTicketById(id: Long): Flow<Ticket?> {
        return ticketDao.getTicketDetailsById(id).map { it?.toDomainModel() }
    }

    override fun getMyTickets(userId: Long): Flow<List<Ticket>> {
        return ticketDao.getTicketsByReporter(userId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun createTicket(ticket: Ticket): Long {
        val entity = TicketEntity(
            title = ticket.title,
            description = ticket.description,
            categoryId = ticket.categoryId,
            locationBuilding = ticket.locationBuilding,
            locationFloor = ticket.locationFloor,
            locationRoom = ticket.locationRoom,
            status = ticket.status.ifBlank { "BARU" },
            imageUrl = ticket.imageUrl,
            reporterId = ticket.reporterId,
            agentId = ticket.agentId,
            createdAt = ticket.createdAt,
            updatedAt = ticket.updatedAt
        )
        return ticketDao.insertTicket(entity)
    }

    override suspend fun updateTicketStatus(ticketId: Long, status: String, agentId: Long?) {
        ticketDao.updateStatus(ticketId, status, agentId)
    }

    override suspend fun toggleSupport(ticketId: Long, userId: Long): Boolean {
        val existing = supportDao.hasUserSupported(ticketId, userId)
        return if (existing > 0) {
            supportDao.deleteSupport(ticketId, userId)
            false
        } else {
            supportDao.insertSupport(
                TicketSupportEntity(ticketId = ticketId, userId = userId)
            )
            true
        }
    }

    override suspend fun softDeleteTicket(ticketId: Long) {
        ticketDao.softDelete(ticketId)
    }

    private fun TicketWithDetails.toDomainModel(): Ticket {
        return Ticket(
            id = id,
            title = title,
            description = description,
            categoryId = categoryId,
            categoryName = categoryName,
            locationBuilding = locationBuilding,
            locationFloor = locationFloor,
            locationRoom = locationRoom,
            status = status,
            imageUrl = imageUrl,
            reporterId = reporterId,
            reporterName = reporterName,
            agentId = agentId,
            agentName = agentName,
            supportCount = supportCount,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
