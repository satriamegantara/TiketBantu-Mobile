package com.example.tiketbantu.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity for Tickets / Complaints.
 * 100% public, priority determined automatically by ticket_supports count (Most Liked).
 */
@Entity(
    tableName = "tickets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["reporter_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["agent_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["reporter_id"]),
        Index(value = ["agent_id"]),
        Index(value = ["status"]),
        Index(value = ["deleted_at"])
    ]
)
data class TicketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "location_building")
    val locationBuilding: String,

    @ColumnInfo(name = "location_floor")
    val locationFloor: String,

    @ColumnInfo(name = "location_room")
    val locationRoom: String,

    @ColumnInfo(name = "status")
    val status: String = "BARU", // BARU, DIPROSES, SELESAI, DITUTUP

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null, // Local absolute path in filesDir

    @ColumnInfo(name = "reporter_id")
    val reporterId: Long,

    @ColumnInfo(name = "agent_id")
    val agentId: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long? = null // Non-null means moved to Trash (Soft delete)
)
