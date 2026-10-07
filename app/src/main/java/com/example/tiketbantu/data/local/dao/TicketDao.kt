package com.example.tiketbantu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.tiketbantu.data.local.entity.TicketEntity
import com.example.tiketbantu.data.local.entity.TicketSupportEntity
import kotlinx.coroutines.flow.Flow

/**
 * Base projection shared by every read query. Uses the named parameter :currentUserId
 * (every query that embeds it must declare that parameter).
 * Soft-deleted tickets (deletedAt != null) are excluded here.
 */
private const val TICKET_META_BASE = """
    SELECT
        t.id AS id,
        t.title AS title,
        t.description AS description,
        t.categoryId AS categoryId,
        c.name AS categoryName,
        t.locationBuilding AS locationBuilding,
        t.locationFloor AS locationFloor,
        t.locationRoom AS locationRoom,
        t.status AS status,
        t.imageUrl AS imageUrl,
        t.reporterId AS reporterId,
        r.name AS reporterName,
        t.agentId AS agentId,
        a.name AS agentName,
        (SELECT COUNT(*) FROM ticket_supports s WHERE s.ticketId = t.id) AS supportCount,
        EXISTS(SELECT 1 FROM ticket_supports s2 WHERE s2.ticketId = t.id AND s2.userId = :currentUserId) AS isSupportedByMe,
        t.createdAt AS createdAt,
        t.updatedAt AS updatedAt
    FROM tickets t
    INNER JOIN categories c ON c.id = t.categoryId
    INNER JOIN users r ON r.id = t.reporterId
    LEFT JOIN users a ON a.id = t.agentId
    WHERE t.deletedAt IS NULL
"""

@Dao
abstract class TicketDao {

    /**
     * Public feed. Rules:
     *  - active tickets (BARU/DIPROSES) first, SELESAI/DITUTUP always in the bottom block;
     *  - inside each block: most supported first when [sortByMostLiked], otherwise newest first
     *    (createdAt DESC is always the tie-breaker);
     *  - [limit] implements infinite scroll (the ViewModel grows it page by page).
     */
    @Query(
        "SELECT * FROM (" + TICKET_META_BASE + ") AS m " +
            "WHERE (:query = '' OR m.title LIKE '%' || :query || '%' OR m.description LIKE '%' || :query || '%') " +
            "AND (:categoryId IS NULL OR m.categoryId = :categoryId) " +
            "AND (:status IS NULL OR m.status = :status) " +
            "ORDER BY CASE WHEN m.status IN ('SELESAI', 'DITUTUP') THEN 1 ELSE 0 END ASC, " +
            "CASE WHEN :sortByMostLiked = 1 THEN m.supportCount ELSE 0 END DESC, " +
            "m.createdAt DESC " +
            "LIMIT :limit"
    )
    abstract fun observeFeed(
        query: String,
        categoryId: Long?,
        status: String?,
        sortByMostLiked: Boolean,
        currentUserId: Long,
        limit: Int
    ): Flow<List<TicketWithMeta>>

    @Query("SELECT * FROM (" + TICKET_META_BASE + ") AS m WHERE m.id = :id")
    abstract fun observeById(id: Long, currentUserId: Long): Flow<TicketWithMeta?>

    @Query(
        "SELECT * FROM (" + TICKET_META_BASE + ") AS m " +
            "WHERE m.reporterId = :currentUserId ORDER BY m.createdAt DESC"
    )
    abstract fun observeMine(currentUserId: Long): Flow<List<TicketWithMeta>>

    @Query(
        "SELECT * FROM (" + TICKET_META_BASE + ") AS m " +
            "WHERE m.isSupportedByMe = 1 ORDER BY m.createdAt DESC"
    )
    abstract fun observeSupported(currentUserId: Long): Flow<List<TicketWithMeta>>

    @Query(
        "SELECT * FROM (" + TICKET_META_BASE + ") AS m " +
            "WHERE (m.agentId = :currentUserId OR (m.status = 'BARU' AND m.agentId IS NULL)) ORDER BY m.createdAt DESC"
    )
    abstract fun observeAgentTasks(currentUserId: Long): Flow<List<TicketWithMeta>>

    @Insert
    abstract suspend fun insertTicket(ticket: TicketEntity): Long

    @Query(
        "UPDATE tickets SET title = :title, description = :description, " +
            "categoryId = :categoryId, updatedAt = :updatedAt WHERE id = :ticketId"
    )
    abstract suspend fun updateTicketContent(
        ticketId: Long,
        title: String,
        description: String,
        categoryId: Long,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query(
        "UPDATE tickets SET status = :status, agentId = COALESCE(:agentId, agentId), " +
            "updatedAt = :updatedAt WHERE id = :ticketId"
    )
    abstract suspend fun updateStatus(
        ticketId: Long,
        status: String,
        agentId: Long?,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE tickets SET deletedAt = :deletedAt WHERE id = :ticketId")
    abstract suspend fun softDelete(ticketId: Long, deletedAt: Long = System.currentTimeMillis())

    // ── "Saya Juga Mengalami" ────────────────────────────────────────────────

    /** Returns the new row id, or -1 when (ticketId, userId) already exists (UNIQUE index). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertSupport(support: TicketSupportEntity): Long

    @Query("DELETE FROM ticket_supports WHERE ticketId = :ticketId AND userId = :userId")
    abstract suspend fun deleteSupport(ticketId: Long, userId: Long): Int

    /**
     * One-tap toggle, atomic so double taps / concurrent calls cannot create duplicates.
     * @return true if the user now supports the ticket, false if the support was removed.
     */
    @Transaction
    open suspend fun toggleSupport(ticketId: Long, userId: Long): Boolean {
        val insertedId = insertSupport(TicketSupportEntity(ticketId = ticketId, userId = userId))
        if (insertedId == -1L) {
            deleteSupport(ticketId, userId)
            return false
        }
        return true
    }
}
