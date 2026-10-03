package com.example.tiketbantu.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity for User accounts.
 * Supported roles: PELAPOR, AGEN, ADMIN.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val email: String,
    val nimNip: String? = null,
    val passwordHash: String,
    val role: String, // PELAPOR, AGEN, ADMIN
    val isActive: Boolean = true
)
