package com.example.tiketbantu.data.repository

import com.example.tiketbantu.domain.model.Comment
import com.example.tiketbantu.domain.repository.AuthRepository
import com.example.tiketbantu.domain.repository.CommentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

/** In-memory comment repository for development / tests (same contract as the Room one). */
class FakeCommentRepository(
    private val authRepository: AuthRepository,
    seed: Boolean = true
) : CommentRepository {

    private val comments = MutableStateFlow(if (seed) FakeCommentSeed.comments() else emptyList())

    override suspend fun getComments(ticketId: Long): List<Comment> =
        comments.value
            .filter { it.ticketId == ticketId }
            .sortedWith(compareBy<Comment> { it.createdAt }.thenBy { it.id })

    override suspend fun addComment(ticketId: Long, userId: Long, content: String): Long {
        val author = authRepository.getCurrentUser().first()?.takeIf { it.id == userId }
        var newId = 0L
        comments.update { current ->
            newId = (current.maxOfOrNull { it.id } ?: 0L) + 1L
            current + Comment(
                id = newId,
                ticketId = ticketId,
                userId = userId,
                userName = author?.name ?: "Pengguna",
                userRole = author?.role ?: "PELAPOR",
                content = content.trim()
            )
        }
        return newId
    }
}

/** Matches FakeSeed in FakeTicketRepository (ticket 1, 2 and 5 have comments). */
private object FakeCommentSeed {
    private const val MINUTE = 60L * 1000L

    fun comments(): List<Comment> {
        val now = System.currentTimeMillis()
        return listOf(
            Comment(1, 1, 10, "Rina Putri", "PELAPOR", "Sudah beberapa hari begini, mohon dicek.", now - 90 * MINUTE),
            Comment(2, 2, 11, "Bima Saputra", "PELAPOR", "AC-nya juga menetes air ke bangku.", now - 240 * MINUTE),
            Comment(3, 2, 20, "Pak Budi", "AGEN", "Teknisi dijadwalkan besok pagi.", now - 200 * MINUTE),
            Comment(4, 5, 20, "Pak Budi", "AGEN", "Kran sudah diganti dan dicek, tidak bocor lagi.", now - 2800 * MINUTE)
        )
    }
}
