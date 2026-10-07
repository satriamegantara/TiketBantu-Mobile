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
    fun validate(user: User, ticket: Ticket, newStatus: String): String? {
        val userRole = user.role.uppercase()
        if (userRole != ROLE_AGEN) {
            return "Hanya Agen Teknisi yang dapat mengklaim atau memperbarui status aduan."
        }
        if (TicketStatus.isFinished(ticket.status)) {
            return "Aduan sudah selesai atau ditutup dan tidak dapat diubah lagi."
        }
        if (newStatus == TicketStatus.DIPROSES) {
            if (ticket.status != TicketStatus.BARU && ticket.status != TicketStatus.DIPROSES) {
                return "Hanya aduan berstatus Baru yang dapat diproses."
            }
            if (ticket.agentId != null && ticket.agentId != user.id) {
                return "Aduan ini sedang ditangani oleh teknisi lain."
            }
            return null
        }
        if (ticket.status != TicketStatus.DIPROSES) {
            return "Aduan harus diklaim dan diproses terlebih dahulu sebelum ditutup/selesai."
        }
        if (ticket.agentId != null && ticket.agentId != user.id) {
            return "Aduan ini sedang ditangani oleh teknisi lain."
        }
        if (newStatus != TicketStatus.SELESAI && newStatus != TicketStatus.DITUTUP) {
            return "Status tujuan tidak valid."
        }
        return null
    }

    /** Whether the status-update actions should be shown to [user] for [ticket]. */
    fun canUpdate(user: User, ticket: Ticket): Boolean {
        if (TicketStatus.isFinished(ticket.status)) return false
        val userRole = user.role.uppercase()
        if (userRole != ROLE_AGEN) return false
        if (ticket.status == TicketStatus.BARU) return true
        if (ticket.status == TicketStatus.DIPROSES) {
            return ticket.agentId == user.id || ticket.agentId == null
        }
        return false
    }
}
