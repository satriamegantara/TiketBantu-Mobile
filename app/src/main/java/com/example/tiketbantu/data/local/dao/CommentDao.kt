package com.example.tiketbantu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tiketbantu.data.local.entity.CommentEntity
import kotlinx.coroutines.flow.Flow

data class CommentWithUser(
    val id: Long,
    val ticketId: Long,
    val userId: Long,
    val userName: String,
    val userRole: String,
    val content: String,
    val createdAt: Long
)

@Dao
interface CommentDao {
    @Query("""
        SELECT 
            c.id AS id,
            c.ticket_id AS ticketId,
            c.user_id AS userId,
            u.name AS userName,
            u.role AS userRole,
            c.content AS content,
            c.created_at AS createdAt
        FROM comments c
        INNER JOIN users u ON c.user_id = u.id
        WHERE c.ticket_id = :ticketId
        ORDER BY c.created_at ASC
    """)
    fun getCommentsForTicket(ticketId: Long): Flow<List<CommentWithUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<CommentEntity>)
}
