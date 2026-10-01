package com.example.tiketbantu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tiketbantu.data.local.entity.CommentEntity

@Dao
interface CommentDao {

    @Query(
        "SELECT cm.id AS id, cm.ticketId AS ticketId, cm.userId AS userId, " +
            "u.name AS userName, u.role AS userRole, cm.content AS content, cm.createdAt AS createdAt " +
            "FROM comments cm INNER JOIN users u ON u.id = cm.userId " +
            "WHERE cm.ticketId = :ticketId " +
            "ORDER BY cm.createdAt ASC, cm.id ASC"
    )
    suspend fun getComments(ticketId: Long): List<CommentWithUser>

    @Insert
    suspend fun insertComment(comment: CommentEntity): Long
}
