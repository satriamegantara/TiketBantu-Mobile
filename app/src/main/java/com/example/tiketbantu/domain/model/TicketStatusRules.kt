package com.example.tiketbantu.domain.model

/**
 * Business rules for the linear status flow (task 3.6):
 *   BARU --(claim by Agen, task 4.6)--> DIPROSES --> SELESAI | DITUTUP (locked afterwards)
 *
 * Only an Agen who is the assigned agent of a DIPROSES ticket may finish/close it.
 * Pure Kotlin, so it is unit-testable and shared by the UI (show/hide buttons) and the use case.
 */
object TicketStatusRules {

    private const val ROLE_AGEN = "AGEN"

    /** @return null when the transition is allowed, otherwise a user-facing reason (Indonesian). */
    fun validate(user: User, ticket: Ticket, newStatus: String): String? = when {
        user.role != ROLE_AGEN ->
            "Hanya Agen yang dapat memperbarui status aduan."
        TicketStatus.isFinished(ticket.status) ->
            "Aduan sudah selesai atau ditutup dan tidak dapat diubah lagi."
        ticket.status != TicketStatus.DIPROSES ->
            "Aduan harus diklaim Agen terlebih dahulu sebelum statusnya diubah."
        ticket.agentId != user.id ->
            "Aduan ini sedang ditangani Agen lain."
        newStatus != TicketStatus.SELESAI && newStatus != TicketStatus.DITUTUP ->
            "Status tujuan tidak valid."
        else -> null
    }

    /** Whether the status-update actions should be shown to [user] for [ticket]. */
    fun canUpdate(user: User, ticket: Ticket): Boolean =
        validate(user, ticket, TicketStatus.SELESAI) == null
}
