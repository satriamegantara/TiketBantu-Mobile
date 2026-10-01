package com.example.tiketbantu.data.local.entity

import androidx.room.ColumnInfo
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

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "nim_nip")
    val nimNip: String? = null,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "role")
    val role: String, // PELAPOR, AGEN, ADMIN

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
