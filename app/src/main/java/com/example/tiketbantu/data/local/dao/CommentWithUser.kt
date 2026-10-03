package com.example.tiketbantu.data.local.dao

/** Comment joined with its author's name and role. Column names match the SQL aliases. */
data class CommentWithUser(
    val id: Long,
    val ticketId: Long,
    val userId: Long,
    val userName: String,
    val userRole: String,
    val content: String,
    val createdAt: Long
)
