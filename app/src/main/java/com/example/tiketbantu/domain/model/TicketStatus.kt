package com.example.tiketbantu.domain.model

/**
 * Canonical ticket status values (stored as String in Room, see TicketEntity.status).
 * Linear flow: BARU -> DIPROSES -> SELESAI / DITUTUP.
 */
object TicketStatus {
    const val BARU = "BARU"
    const val DIPROSES = "DIPROSES"
    const val SELESAI = "SELESAI"
    const val DITUTUP = "DITUTUP"

    /** Finished tickets are locked and always shown at the bottom of the feed. */
    fun isFinished(status: String): Boolean = status == SELESAI || status == DITUTUP
}
