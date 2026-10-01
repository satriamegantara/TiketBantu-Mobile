package com.example.tiketbantu.domain.model

/**
 * Domain model representing a user (Pelapor, Agen, or Admin).
 */
data class User(
    val id: Long = 0,
    val name: String,
    val email: String,
    val nimNip: String? = null,
    val role: String, // PELAPOR, AGEN, ADMIN
    val isActive: Boolean = true
)
