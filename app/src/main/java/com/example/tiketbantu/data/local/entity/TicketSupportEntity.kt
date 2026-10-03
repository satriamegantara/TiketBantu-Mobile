package com.example.tiketbantu.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table "ticket_supports" ("Saya Juga Mengalami").
 * UNIQUE(ticketId, userId) guarantees 1 user = 1 support per ticket.
 */
@Entity(
    tableName = "ticket_supports",
    foreignKeys = [
        ForeignKey(
            entity = TicketEntity::class,
            parentColumns = ["id"],
            childColumns = ["ticketId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["ticketId", "userId"], unique = true),
        Index(value = ["userId"])
    ]
)
data class TicketSupportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val ticketId: Long,
    val userId: Long,
    val createdAt: Long = System.currentTimeMillis()
)
