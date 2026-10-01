package com.example.tiketbantu.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TicketStatusRulesTest {

    private val agent = User(id = 20, name = "Pak Budi", email = "budi@kampus.ac.id", role = "AGEN")
    private val otherAgent = User(id = 21, name = "Bu Sari", email = "sari@kampus.ac.id", role = "AGEN")
    private val reporter = User(id = 1, name = "Demo", email = "demo@kampus.ac.id", role = "PELAPOR")
    private val admin = User(id = 2, name = "Admin", email = "admin@kampus.ac.id", role = "ADMIN")

    private fun ticket(status: String, agentId: Long? = 20) = Ticket(
        id = 1,
        title = "t",
        description = "d",
        categoryId = 1,
        locationBuilding = "A",
        locationFloor = "1",
        locationRoom = "101",
        status = status,
        reporterId = 10,
        agentId = agentId
    )

    @Test
    fun assignedAgentCanFinishAndCloseProcessedTicket() {
        val t = ticket(TicketStatus.DIPROSES)
        assertNull(TicketStatusRules.validate(agent, t, TicketStatus.SELESAI))
        assertNull(TicketStatusRules.validate(agent, t, TicketStatus.DITUTUP))
        assertTrue(TicketStatusRules.canUpdate(agent, t))
    }

    @Test
    fun nonAgentRolesCannotUpdate() {
        val t = ticket(TicketStatus.DIPROSES)
        assertNotNull(TicketStatusRules.validate(reporter, t, TicketStatus.SELESAI))
        assertNotNull(TicketStatusRules.validate(admin, t, TicketStatus.SELESAI))
        assertFalse(TicketStatusRules.canUpdate(reporter, t))
    }

    @Test
    fun finishedTicketsAreLocked() {
        assertNotNull(TicketStatusRules.validate(agent, ticket(TicketStatus.SELESAI), TicketStatus.DITUTUP))
        assertNotNull(TicketStatusRules.validate(agent, ticket(TicketStatus.DITUTUP), TicketStatus.SELESAI))
    }

    @Test
    fun newTicketMustBeClaimedFirst() {
        assertNotNull(TicketStatusRules.validate(agent, ticket(TicketStatus.BARU, agentId = null), TicketStatus.SELESAI))
    }

    @Test
    fun otherAgentCannotTakeOverTicket() {
        assertNotNull(TicketStatusRules.validate(otherAgent, ticket(TicketStatus.DIPROSES), TicketStatus.SELESAI))
    }

    @Test
    fun targetStatusMustBeFinalStatus() {
        val t = ticket(TicketStatus.DIPROSES)
        assertNotNull(TicketStatusRules.validate(agent, t, TicketStatus.BARU))
        assertNotNull(TicketStatusRules.validate(agent, t, TicketStatus.DIPROSES))
    }
}
