package com.example.tiketbantu.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a single ticket/aduan in the system.
 */
@Serializable
data class Ticket(
    val id: String,
    val title: String,
    val description: String,
    val status: String = "OPEN",
    val authorId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val likedBy: List<String> = emptyList()
)
