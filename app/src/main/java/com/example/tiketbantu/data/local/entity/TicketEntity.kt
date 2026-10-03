package com.example.tiketbantu.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity for Tickets / Complaints.
 * [imageUrl] holds a local file path in filesDir, not a remote URL.
 */
@Entity(
    tableName = "tickets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["reporterId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["agentId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["reporterId"]),
        Index(value = ["agentId"]),
        Index(value = ["status"]),
        Index(value = ["createdAt"]),
        Index(value = ["deletedAt"])
    ]
)
data class TicketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String,
    val categoryId: Long,
    val locationBuilding: String,
    val locationFloor: String,
    val locationRoom: String,
    val status: String = "BARU", // BARU, DIPROSES, SELESAI, DITUTUP
    val imageUrl: String? = null,
    val reporterId: Long,
    val agentId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)
