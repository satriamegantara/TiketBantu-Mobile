package com.example.tiketbantu.data.repository

import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * In-memory repository for development / previews / unit tests. It follows the same
 * contract as the Room implementation (ordering, support toggle, soft delete), so UI work
 * does not depend on AppDatabase being ready.
 */
class FakeTicketRepository(seed: Boolean = true) : TicketRepository {

    private val tickets = MutableStateFlow(if (seed) FakeSeed.tickets() else emptyList())

    /** Set of (ticketId, userId) pairs, mirrors UNIQUE(ticketId, userId). */
    private val supports = MutableStateFlow(if (seed) FakeSeed.supports() else emptySet())

    private fun observeEnriched(currentUserId: Long): Flow<List<Ticket>> =
        combine(tickets, supports) { list, supportSet ->
            val countByTicket = supportSet.groupingBy { it.first }.eachCount()
            list.map { ticket ->
                ticket.copy(
                    supportCount = countByTicket[ticket.id] ?: 0,
                    isSupportedByMe = (ticket.id to currentUserId) in supportSet
                )
            }
        }

    override fun getAllTickets(
        query: String,
        categoryId: Long?,
        status: String?,
        sortByMostLiked: Boolean,
        currentUserId: Long,
        limit: Int
    ): Flow<List<Ticket>> {
        val comparator = compareBy<Ticket> { if (TicketStatus.isFinished(it.status)) 1 else 0 }
            .let { base ->
                if (sortByMostLiked) base.thenByDescending { it.supportCount } else base
            }
            .thenByDescending { it.createdAt }

        return observeEnriched(currentUserId).map { list ->
            list.asSequence()
                .filter { query.isBlank() || it.title.contains(query, true) || it.description.contains(query, true) }
                .filter { categoryId == null || it.categoryId == categoryId }
                .filter { status.isNullOrBlank() || it.status.equals(status, ignoreCase = true) }
                .sortedWith(comparator)
                .take(limit)
                .toList()
        }
    }

    override fun getTicketById(id: Long, currentUserId: Long): Flow<Ticket?> =
        observeEnriched(currentUserId).map { list -> list.find { it.id == id } }

    override fun getMyTickets(userId: Long): Flow<List<Ticket>> =
        observeEnriched(userId).map { list ->
            list.filter { it.reporterId == userId }.sortedByDescending { it.createdAt }
        }

    override fun getSupportedTickets(userId: Long): Flow<List<Ticket>> =
        observeEnriched(userId).map { list ->
            list.filter { it.isSupportedByMe }.sortedByDescending { it.createdAt }
        }

    override suspend fun updateTicketContent(
        ticketId: Long,
        title: String,
        description: String,
        categoryId: Long
    ) {
        tickets.update { current ->
            current.map { ticket ->
                if (ticket.id == ticketId) {
                    ticket.copy(
                        title = title,
                        description = description,
                        categoryId = categoryId,
                        updatedAt = System.currentTimeMillis()
                    )
                } else ticket
            }
        }
    }

    override suspend fun createTicket(ticket: Ticket): Long {
        var newId = 0L
        tickets.update { current ->
            newId = (current.maxOfOrNull { it.id } ?: 0L) + 1L
            listOf(ticket.copy(id = newId)) + current
        }
        return newId
    }

    override suspend fun updateTicketStatus(ticketId: Long, status: String, agentId: Long?) {
        tickets.update { current ->
            current.map { ticket ->
                if (ticket.id == ticketId) {
                    ticket.copy(
                        status = status,
                        agentId = agentId ?: ticket.agentId,
                        updatedAt = System.currentTimeMillis()
                    )
                } else {
                    ticket
                }
            }
        }
    }

    override suspend fun toggleSupport(ticketId: Long, userId: Long): Boolean {
        val key = ticketId to userId
        var nowSupported = false
        supports.update { current ->
            if (key in current) {
                nowSupported = false
                current - key
            } else {
                nowSupported = true
                current + key
            }
        }
        return nowSupported
    }

    override suspend fun softDeleteTicket(ticketId: Long) {
        tickets.update { current -> current.filterNot { it.id == ticketId } }
    }
}

/** Demo data: 3 categories (1 IT, 2 Ruangan, 3 Umum); demo user id = 1 (see FakeAuthRepository). */
private object FakeSeed {
    private const val HOUR = 60L * 60L * 1000L

    fun tickets(): List<Ticket> {
        val now = System.currentTimeMillis()
        return listOf(
            ticket(1, "Wi-Fi gedung A sering putus", "Sinyal hilang tiap beberapa menit, terutama saat kuliah daring.", 1, "Teknologi & IT", "Gedung A", "2", "201", TicketStatus.BARU, 10, "Rina Putri", null, now - 2 * HOUR),
            ticket(2, "AC ruang kuliah tidak dingin", "AC menyala tapi hanya mengeluarkan angin biasa, ruangan pengap.", 2, "Fasilitas Ruangan", "Gedung B", "1", "105", TicketStatus.DIPROSES, 11, "Bima Saputra", 20L to "Pak Budi", now - 5 * HOUR),
            ticket(3, "Proyektor LCD ruang 303 mati", "Proyektor tidak menyala sejak awal pekan, kabel sudah dicek.", 1, "Teknologi & IT", "Gedung C", "3", "303", TicketStatus.BARU, 12, "Dewi Lestari", null, now - 9 * HOUR),
            ticket(4, "Lampu koridor lantai 3 padam", "Koridor gelap saat sore, berbahaya untuk tangga.", 3, "Infrastruktur Umum", "Gedung A", "3", "Koridor", TicketStatus.BARU, 13, "Andi Wijaya", null, now - 26 * HOUR),
            ticket(5, "Kran air toilet bocor", "Air menetes terus dan lantai licin.", 3, "Infrastruktur Umum", "Gedung B", "1", "Toilet", TicketStatus.SELESAI, 10, "Rina Putri", 20L to "Pak Budi", now - 50 * HOUR),
            ticket(6, "Kursi kuliah patah", "Dua kursi di barisan belakang patah dan tidak aman dipakai.", 2, "Fasilitas Ruangan", "Gedung C", "2", "210", TicketStatus.DITUTUP, 12, "Dewi Lestari", 21L to "Bu Sari", now - 70 * HOUR),
            ticket(7, "Stop kontak lab komputer longgar", "Beberapa stop kontak longgar dan sempat berasap.", 1, "Teknologi & IT", "Gedung D", "1", "Lab Komputer", TicketStatus.DIPROSES, 11, "Bima Saputra", 21L to "Bu Sari", now - 30 * HOUR)
        )
    }

    fun supports(): Set<Pair<Long, Long>> = buildSet {
        listOf(11L, 12L, 13L, 14L, 15L).forEach { add(1L to it) }
        listOf(1L, 11L, 12L, 13L).forEach { add(2L to it) }
        add(3L to 14L)
        listOf(11L, 12L).forEach { add(5L to it) }
        add(6L to 13L)
        listOf(11L, 12L, 13L, 14L, 15L, 16L).forEach { add(7L to it) }
    }

    private fun ticket(
        id: Long, title: String, description: String,
        categoryId: Long, categoryName: String,
        building: String, floor: String, room: String,
        status: String, reporterId: Long, reporterName: String,
        agent: Pair<Long, String>?, createdAt: Long
    ) = Ticket(
        id = id, title = title, description = description,
        categoryId = categoryId, categoryName = categoryName,
        locationBuilding = building, locationFloor = floor, locationRoom = room,
        status = status, reporterId = reporterId, reporterName = reporterName,
        agentId = agent?.first, agentName = agent?.second,
        createdAt = createdAt, updatedAt = createdAt
    )
}
