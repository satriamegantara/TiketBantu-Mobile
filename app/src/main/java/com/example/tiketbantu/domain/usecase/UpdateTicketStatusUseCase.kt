package com.example.tiketbantu.domain.usecase

import com.example.tiketbantu.domain.model.TicketStatusRules
import com.example.tiketbantu.domain.model.User
import com.example.tiketbantu.domain.repository.TicketRepository
import kotlinx.coroutines.flow.first

/**
 * Moves a ticket from DIPROSES to SELESAI / DITUTUP. The ticket is re-read from the repository
 * right before writing, so a stale screen can never bypass the lock on finished tickets.
 */
class UpdateTicketStatusUseCase(
    private val repository: TicketRepository
) {
    suspend operator fun invoke(ticketId: Long, newStatus: String, user: User): Result<Unit> {
        val ticket = repository.getTicketById(ticketId, user.id).first()
            ?: return Result.failure(IllegalStateException("Aduan tidak ditemukan."))

        val error = TicketStatusRules.validate(user, ticket, newStatus)
        if (error != null) return Result.failure(IllegalStateException(error))

        val agentId = if (newStatus == com.example.tiketbantu.domain.model.TicketStatus.DIPROSES) user.id else ticket.agentId
        repository.updateTicketStatus(ticketId, newStatus, agentId)
        return Result.success(Unit)
    }
}
