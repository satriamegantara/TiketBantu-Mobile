package com.example.tiketbantu.domain.repository

import com.example.tiketbantu.domain.model.Comment

/** Domain repository contract for the comment thread of a ticket. */
interface CommentRepository {
    /** One-shot read, oldest first. Called on open, after posting, and by the 5s polling loop. */
    suspend fun getComments(ticketId: Long): List<Comment>

    /** @return the new comment id. */
    suspend fun addComment(ticketId: Long, userId: Long, content: String): Long
}
