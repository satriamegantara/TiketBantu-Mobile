package com.example.tiketbantu.domain.model

/**
 * Domain model representing a public campus ticket / complaint.
 * 100% public, urgency determined by supportCount ("Saya Juga Mengalami").
 */
data class Ticket(
    val id: Long = 0,
    val title: String,
    val description: String,
    val categoryId: Long,
    val categoryName: String = "",
    val locationBuilding: String,
    val locationFloor: String,
    val locationRoom: String,
    val status: String = "BARU", // BARU, DIPROSES, SELESAI, DITUTUP
    val imageUrl: String? = null,
    val reporterId: Long,
    val reporterName: String = "",
    val agentId: Long? = null,
    val agentName: String? = null,
    val supportCount: Int = 0,
    val isSupportedByMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
