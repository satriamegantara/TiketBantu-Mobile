package com.example.tiketbantu.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Table "users" (role: PELAPOR | AGEN | ADMIN). Schema follows CLAUDE_TiketBantu.md. */
@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val nimNip: String? = null,
    val passwordHash: String,
    val role: String,
    val isActive: Boolean = true
)
