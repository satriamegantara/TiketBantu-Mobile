package com.example.tiketbantu.data.local.dao

/**
 * Read model produced by the joined queries in [TicketDao]:
 * ticket + category/reporter/agent names + support aggregate for the current user.
 * Column names must match the aliases in the SQL.
 */
data class TicketWithMeta(
    val id: Long,
    val title: String,
    val description: String,
    val categoryId: Long,
    val categoryName: String,
    val locationBuilding: String,
    val locationFloor: String,
    val locationRoom: String,
    val status: String,
    val imageUrl: String?,
    val reporterId: Long,
    val reporterName: String,
    val agentId: Long?,
    val agentName: String?,
    val supportCount: Int,
    val isSupportedByMe: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
