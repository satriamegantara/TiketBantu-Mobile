package com.example.tiketbantu.domain.model

data class Comment(
    val id: Long = 0,
    val ticketId: Long,
    val userId: Long,
    val userName: String = "",
    val userRole: String = "",
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
