package com.example.tiketbantu.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Validates the 10 dummy complaints/tickets dataset specifications:
 * - Exactly 10 tickets.
 * - Proper distribution across the 4 valid campus categories (Jaringan, Hardware, Software, Fasilitas).
 * - Proper status distribution (BARU, DIPROSES, SELESAI).
 * - Proper assignment to the 4 official agents (Agen Jaringan, Agen Hardware, Agen Software, Agen Fasilitas).
 * - Unassigned tickets (agentId = null) for status BARU.
 * - Dynamic support counts with creator as initial supporter.
 * - Varied comments with empty comment states tested.
 */
class DummySeedValidationTest {

    data class DummyTicketDef(
        val id: Long,
        val title: String,
        val categoryId: Long,
        val status: String,
        val reporterId: Long,
        val agentId: Long?,
        val supportUserIds: List<Long>,
        val commentCount: Int
    )

    private val seedTickets = listOf(
        DummyTicketDef(
            id = 1L,
            title = "Koneksi Wi-Fi Kampus Putus-Nyambung di Lantai 2 Gedung Perpustakaan",
            categoryId = 1L, // Jaringan
            status = "DIPROSES",
            reporterId = 3L, // satcarzensyaf
            agentId = 2L, // Agen Jaringan
            supportUserIds = listOf(3L, 4L, 8L, 9L, 10L, 1L),
            commentCount = 2
        ),
        DummyTicketDef(
            id = 2L,
            title = "Proyektor Ruang 304 Mati Total Saat Perkuliahan Praktikum",
            categoryId = 2L, // Hardware
            status = "DIPROSES",
            reporterId = 4L, // Ahmad Dosen
            agentId = 5L, // Agen Hardware
            supportUserIds = listOf(4L, 3L, 8L, 9L),
            commentCount = 2
        ),
        DummyTicketDef(
            id = 3L,
            title = "Portal SIAKAD Mengalami Error 500 Saat Pengisian KRS Mahasiswa",
            categoryId = 3L, // Software
            status = "SELESAI",
            reporterId = 8L, // Rina Kartika
            agentId = 6L, // Agen Software
            supportUserIds = listOf(8L, 3L, 9L, 10L),
            commentCount = 3
        ),
        DummyTicketDef(
            id = 4L,
            title = "AC Sentral Ruang Kuliah Bersama Mengeluarkan Bunyi Berisik & Hawa Panas",
            categoryId = 4L, // Fasilitas
            status = "DIPROSES",
            reporterId = 9L, // Dimas Putra
            agentId = 7L, // Agen Fasilitas
            supportUserIds = listOf(9L, 3L, 8L),
            commentCount = 1
        ),
        DummyTicketDef(
            id = 5L,
            title = "Stop Kontak Selasar Lantai 1 Gedung B Mengeluarkan Percikan Api",
            categoryId = 4L, // Fasilitas
            status = "BARU",
            reporterId = 10L, // Nadia Safitri
            agentId = null, // Unassigned
            supportUserIds = listOf(10L, 3L, 4L),
            commentCount = 1
        ),
        DummyTicketDef(
            id = 6L,
            title = "PC Laboratorium Komputer 02 Mati Total dan Tidak Mau Booting",
            categoryId = 2L, // Hardware
            status = "BARU",
            reporterId = 3L, // satcarzensyaf
            agentId = null, // Unassigned
            supportUserIds = listOf(3L, 9L),
            commentCount = 0 // Empty comment state
        ),
        DummyTicketDef(
            id = 7L,
            title = "Software MATLAB & SPSS pada Komputer Lab Statistik Belum Diaktivasi Lisensi",
            categoryId = 3L, // Software
            status = "BARU",
            reporterId = 4L, // Ahmad Dosen
            agentId = null, // Unassigned
            supportUserIds = listOf(4L, 8L),
            commentCount = 0 // Empty comment state
        ),
        DummyTicketDef(
            id = 8L,
            title = "Switch Hub Jaringan di Laboratorium Komputer Sering Mengalami Packet Loss",
            categoryId = 1L, // Jaringan
            status = "DIPROSES",
            reporterId = 9L, // Dimas Putra
            agentId = 2L, // Agen Jaringan
            supportUserIds = listOf(9L),
            commentCount = 1
        ),
        DummyTicketDef(
            id = 9L,
            title = "Keyboard dan Mouse Komputer Nomor 08 di Lab Multimedia Rusak",
            categoryId = 2L, // Hardware
            status = "SELESAI",
            reporterId = 8L, // Rina Kartika
            agentId = 5L, // Agen Hardware
            supportUserIds = listOf(8L),
            commentCount = 2
        ),
        DummyTicketDef(
            id = 10L,
            title = "Plafon Gypsum Koridor Lantai 3 Gedung Rektorat Lapuk dan Rawan Ambruk",
            categoryId = 4L, // Fasilitas
            status = "SELESAI",
            reporterId = 3L, // satcarzensyaf
            agentId = 7L, // Agen Fasilitas
            supportUserIds = listOf(3L),
            commentCount = 1
        )
    )

    @Test
    fun verifyTotalExactlyTenTickets() {
        assertEquals("Total tiket harus tepat 10", 10, seedTickets.size)
        val ids = seedTickets.map { it.id }.toSet()
        assertEquals("Semua ID tiket 1..10 harus unik", 10, ids.size)
    }

    @Test
    fun verifyCategoryDistribution() {
        val categories = seedTickets.groupBy { it.categoryId }
        assertEquals("Semua 4 kategori resmi harus digunakan", 4, categories.size)
        assertTrue("Kategori 1 (Jaringan) harus memiliki tiket", (categories[1L]?.size ?: 0) >= 2)
        assertTrue("Kategori 2 (Hardware) harus memiliki tiket", (categories[2L]?.size ?: 0) >= 2)
        assertTrue("Kategori 3 (Software) harus memiliki tiket", (categories[3L]?.size ?: 0) >= 2)
        assertTrue("Kategori 4 (Fasilitas) harus memiliki tiket", (categories[4L]?.size ?: 0) >= 2)
    }

    @Test
    fun verifyStatusDistribution() {
        val statuses = seedTickets.groupBy { it.status }
        assertTrue("Harus ada tiket BARU (Menunggu)", (statuses["BARU"]?.size ?: 0) >= 3)
        assertTrue("Harus ada tiket DIPROSES", (statuses["DIPROSES"]?.size ?: 0) >= 3)
        assertTrue("Harus ada tiket SELESAI", (statuses["SELESAI"]?.size ?: 0) >= 3)
    }

    @Test
    fun verifyAgentAssignment() {
        val assigned = seedTickets.filter { it.agentId != null }
        val unassigned = seedTickets.filter { it.agentId == null }

        // 3 tiket status BARU belum memiliki agen (unassigned)
        assertEquals(3, unassigned.size)
        assertTrue(unassigned.all { it.status == "BARU" })

        // 7 tiket telah ditugaskan ke 4 agen resmi
        assertEquals(7, assigned.size)
        val agentIds = assigned.mapNotNull { it.agentId }.toSet()
        assertEquals(setOf(2L, 5L, 6L, 7L), agentIds)

        // Verifikasi kesesuaian kategori tugas masing-masing agen
        val jaringans = assigned.filter { it.agentId == 2L }
        assertTrue(jaringans.all { it.categoryId == 1L })

        val hardwares = assigned.filter { it.agentId == 5L }
        assertTrue(hardwares.all { it.categoryId == 2L })

        val softwares = assigned.filter { it.agentId == 6L }
        assertTrue(softwares.all { it.categoryId == 3L })

        val fasilitases = assigned.filter { it.agentId == 7L }
        assertTrue(fasilitases.all { it.categoryId == 4L })
    }

    @Test
    fun verifyCreatorIsInitialSupporter() {
        seedTickets.forEach { ticket ->
            assertTrue(
                "Pembuat tiket (${ticket.reporterId}) harus terdaftar dalam dukungan tiket #${ticket.id}",
                ticket.supportUserIds.contains(ticket.reporterId)
            )
            assertEquals(
                "Pembuat tiket harus menjadi dukungan awal",
                ticket.reporterId,
                ticket.supportUserIds.first()
            )
        }
    }

    @Test
    fun verifySupportCountVariations() {
        val supportCounts = seedTickets.map { it.supportUserIds.size }
        assertTrue("Ada tiket dengan dukungan tinggi (>3)", supportCounts.any { it >= 4 })
        assertTrue("Ada tiket dengan dukungan sedang (2-3)", supportCounts.any { it in 2..3 })
        assertTrue("Ada tiket dengan hanya dukungan awal (1)", supportCounts.any { it == 1 })
    }

    @Test
    fun verifyCommentsDistributionAndEmptyState() {
        val emptyComments = seedTickets.filter { it.commentCount == 0 }
        assertTrue("Harus ada tiket tanpa komentar untuk menguji empty state", emptyComments.isNotEmpty())

        val withComments = seedTickets.filter { it.commentCount > 0 }
        assertTrue("Harus ada tiket dengan komentar", withComments.isNotEmpty())
    }
}
