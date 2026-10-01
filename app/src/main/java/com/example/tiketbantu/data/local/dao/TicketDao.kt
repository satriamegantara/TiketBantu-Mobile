package com.example.tiketbantu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tiketbantu.data.local.entity.TicketEntity
import kotlinx.coroutines.flow.Flow

data class TicketWithDetails(
    val id: Long,
    val title: String,
    val description: String,
    val categoryId: Long,
    val categoryName: String,
    val locationBuilding: String,
    val locationFloor: String,
    val locationRoom: String,
    val status: String,
    val imageUrl: String?,
    val reporterId: Long,
    val reporterName: String,
    val agentId: Long?,
    val agentName: String?,
    val supportCount: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?
)

@Dao
interface TicketDao {

    @Query("""
        SELECT 
            t.id AS id,
            t.title AS title,
            t.description AS description,
            t.category_id AS categoryId,
            c.name AS categoryName,
            t.location_building AS locationBuilding,
            t.location_floor AS locationFloor,
            t.location_room AS locationRoom,
            t.status AS status,
            t.image_url AS imageUrl,
            t.reporter_id AS reporterId,
            u_rep.name AS reporterName,
            t.agent_id AS agentId,
            u_age.name AS agentName,
            (SELECT COUNT(*) FROM ticket_supports s WHERE s.ticket_id = t.id) AS supportCount,
            t.created_at AS createdAt,
            t.updated_at AS updatedAt,
            t.deleted_at AS deletedAt
        FROM tickets t
        INNER JOIN categories c ON t.category_id = c.id
        INNER JOIN users u_rep ON t.reporter_id = u_rep.id
        LEFT JOIN users u_age ON t.agent_id = u_age.id
        WHERE t.deleted_at IS NULL
          AND (:categoryId IS NULL OR t.category_id = :categoryId)
          AND (:status IS NULL OR t.status = :status)
          AND (:query = '' OR t.title LIKE '%' || :query || '%' OR t.description LIKE '%' || :query || '%')
        ORDER BY 
            CASE WHEN t.status IN ('SELESAI', 'DITUTUP') THEN 1 ELSE 0 END ASC,
            CASE WHEN :sortByMostLiked = 1 THEN (SELECT COUNT(*) FROM ticket_supports s WHERE s.ticket_id = t.id) END DESC,
            t.created_at DESC
    """)
    fun getFeedTickets(
        query: String = "",
        categoryId: Long? = null,
        status: String? = null,
        sortByMostLiked: Int = 0 // 1 = true, 0 = false
    ): Flow<List<TicketWithDetails>>

    @Query("""
        SELECT 
            t.id AS id,
            t.title AS title,
            t.description AS description,
            t.category_id AS categoryId,
            c.name AS categoryName,
            t.location_building AS locationBuilding,
            t.location_floor AS locationFloor,
            t.location_room AS locationRoom,
            t.status AS status,
            t.image_url AS imageUrl,
            t.reporter_id AS reporterId,
            u_rep.name AS reporterName,
            t.agent_id AS agentId,
            u_age.name AS agentName,
            (SELECT COUNT(*) FROM ticket_supports s WHERE s.ticket_id = t.id) AS supportCount,
            t.created_at AS createdAt,
            t.updated_at AS updatedAt,
            t.deleted_at AS deletedAt
        FROM tickets t
        INNER JOIN categories c ON t.category_id = c.id
        INNER JOIN users u_rep ON t.reporter_id = u_rep.id
        LEFT JOIN users u_age ON t.agent_id = u_age.id
        WHERE t.id = :id AND t.deleted_at IS NULL
        LIMIT 1
    """)
    fun getTicketDetailsById(id: Long): Flow<TicketWithDetails?>

    @Query("""
        SELECT 
            t.id AS id,
            t.title AS title,
            t.description AS description,
            t.category_id AS categoryId,
            c.name AS categoryName,
            t.location_building AS locationBuilding,
            t.location_floor AS locationFloor,
            t.location_room AS locationRoom,
            t.status AS status,
            t.image_url AS imageUrl,
            t.reporter_id AS reporterId,
            u_rep.name AS reporterName,
            t.agent_id AS agentId,
            u_age.name AS agentName,
            (SELECT COUNT(*) FROM ticket_supports s WHERE s.ticket_id = t.id) AS supportCount,
            t.created_at AS createdAt,
            t.updated_at AS updatedAt,
            t.deleted_at AS deletedAt
        FROM tickets t
        INNER JOIN categories c ON t.category_id = c.id
        INNER JOIN users u_rep ON t.reporter_id = u_rep.id
        LEFT JOIN users u_age ON t.agent_id = u_age.id
        WHERE t.reporter_id = :userId AND t.deleted_at IS NULL
        ORDER BY t.created_at DESC
    """)
    fun getTicketsByReporter(userId: Long): Flow<List<TicketWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tickets: List<TicketEntity>)

    @Update
    suspend fun updateTicket(ticket: TicketEntity)

    @Query("UPDATE tickets SET status = :status, agent_id = :agentId, updated_at = :updatedAt WHERE id = :ticketId")
    suspend fun updateStatus(ticketId: Long, status: String, agentId: Long?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE tickets SET deleted_at = :deletedAt WHERE id = :ticketId")
    suspend fun softDelete(ticketId: Long, deletedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM tickets WHERE deleted_at IS NULL AND status = :status")
    fun countByStatus(status: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM tickets WHERE deleted_at IS NULL")
    fun countTotalActive(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tickets")
    suspend fun countAll(): Int
}
