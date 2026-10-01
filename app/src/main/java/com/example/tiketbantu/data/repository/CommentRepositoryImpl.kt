package com.example.tiketbantu.data.repository

import com.example.tiketbantu.data.local.dao.CommentDao
import com.example.tiketbantu.data.local.entity.CommentEntity
import com.example.tiketbantu.domain.model.Comment
import com.example.tiketbantu.domain.repository.CommentRepository

/** Room-backed comment repository. */
class CommentRepositoryImpl(
    private val commentDao: CommentDao
) : CommentRepository {

    override suspend fun getComments(ticketId: Long): List<Comment> =
        commentDao.getComments(ticketId).map { row ->
            Comment(
                id = row.id,
                ticketId = row.ticketId,
                userId = row.userId,
                userName = row.userName,
                userRole = row.userRole,
                content = row.content,
                createdAt = row.createdAt
            )
        }

    override suspend fun addComment(ticketId: Long, userId: Long, content: String): Long =
        commentDao.insertComment(
            CommentEntity(
                ticketId = ticketId,
                userId = userId,
                content = content.trim(),
                createdAt = System.currentTimeMillis()
            )
        )
}
