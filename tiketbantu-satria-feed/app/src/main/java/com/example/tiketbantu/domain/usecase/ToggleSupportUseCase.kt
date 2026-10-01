package com.example.tiketbantu.domain.usecase

import com.example.tiketbantu.domain.repository.TicketRepository

/**
 * Toggles "Saya Juga Mengalami" for a user on a ticket (one support per user per ticket).
 * @return true if the ticket is now supported by the user, false if the support was removed.
 */
class ToggleSupportUseCase(
    private val repository: TicketRepository
) {
    suspend operator fun invoke(ticketId: Long, userId: Long): Boolean =
        repository.toggleSupport(ticketId, userId)
}
