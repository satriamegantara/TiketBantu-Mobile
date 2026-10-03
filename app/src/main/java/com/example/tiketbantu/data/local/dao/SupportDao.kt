package com.example.tiketbantu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tiketbantu.data.local.entity.TicketSupportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SupportDao {
    @Query("SELECT COUNT(*) FROM ticket_supports WHERE ticket_id = :ticketId")
    fun countSupportsForTicket(ticketId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM ticket_supports WHERE ticket_id = :ticketId AND user_id = :userId")
    suspend fun hasUserSupported(ticketId: Long, userId: Long): Int

    @Query("SELECT ticket_id FROM ticket_supports WHERE user_id = :userId")
    fun getSupportedTicketIdsByUser(userId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSupport(support: TicketSupportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(supports: List<TicketSupportEntity>)

    @Query("DELETE FROM ticket_supports WHERE ticket_id = :ticketId AND user_id = :userId")
    suspend fun deleteSupport(ticketId: Long, userId: Long): Int
}
