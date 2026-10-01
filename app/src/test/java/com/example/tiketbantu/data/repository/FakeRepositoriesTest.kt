package com.example.tiketbantu.data.repository

import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.domain.usecase.UpdateTicketStatusUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Verifies the feed / support / comment / status contract on the in-memory fakes (demo seed). */
class FakeRepositoriesTest {

    private val demoUserId = 1L

    @Test
    fun latestSortKeepsFinishedTicketsAtTheBottom() = runBlocking<Unit> {
        val ids = FakeTicketRepository()
            .getAllTickets(currentUserId = demoUserId)
            .first()
            .map { it.id }
        assertEquals(listOf(1L, 2L, 3L, 4L, 7L, 5L, 6L), ids)
    }

    @Test
    fun mostLikedSortOrdersBySupportWithinEachBlock() = runBlocking<Unit> {
        val ids = FakeTicketRepository()
            .getAllTickets(sortByMostLiked = true, currentUserId = demoUserId)
            .first()
            .map { it.id }
        assertEquals(listOf(7L, 1L, 2L, 3L, 4L, 5L, 6L), ids)
    }

    @Test
    fun searchStatusFilterAndLimitWork() = runBlocking<Unit> {
        val repo = FakeTicketRepository()
        assertEquals(listOf(1L), repo.getAllTickets(query = "wi-fi").first().map { it.id })
        assertEquals(
            listOf(2L, 7L),
            repo.getAllTickets(status = TicketStatus.DIPROSES).first().map { it.id }
        )
        assertEquals(3, repo.getAllTickets(limit = 3).first().size)
    }

    @Test
    fun toggleSupportAddsThenRemovesSingleSupport() = runBlocking<Unit> {
        val repo = FakeTicketRepository()
        val before = repo.getTicketById(3, demoUserId).first()!!
        assertFalse(before.isSupportedByMe)
        assertEquals(1, before.supportCount)

        assertTrue(repo.toggleSupport(3, demoUserId))
        val after = repo.getTicketById(3, demoUserId).first()!!
        assertTrue(after.isSupportedByMe)
        assertEquals(2, after.supportCount)

        assertFalse(repo.toggleSupport(3, demoUserId))
        assertEquals(1, repo.getTicketById(3, demoUserId).first()!!.supportCount)
    }

    @Test
    fun addCommentAppendsAtTheEndOfTheThread() = runBlocking<Unit> {
        val comments = FakeCommentRepository(FakeAuthRepository())
        assertEquals(1, comments.getComments(1).size)

        comments.addComment(1, demoUserId, "  Terima kasih  ")
        val thread = comments.getComments(1)
        assertEquals(2, thread.size)
        assertEquals("Terima kasih", thread.last().content)
        assertEquals("Pengguna Demo", thread.last().userName)
    }

    @Test
    fun agentCanCloseOwnTicketOnlyOnce() = runBlocking<Unit> {
        val tickets = FakeTicketRepository()
        val useCase = UpdateTicketStatusUseCase(tickets)
        val agent = FakeAuthRepository(role = "AGEN").getCurrentUser().first()!!

        assertTrue(useCase(2, TicketStatus.SELESAI, agent).isSuccess)
        assertEquals(TicketStatus.SELESAI, tickets.getTicketById(2, agent.id).first()!!.status)

        // Locked after finishing.
        assertTrue(useCase(2, TicketStatus.DITUTUP, agent).isFailure)
    }
}
