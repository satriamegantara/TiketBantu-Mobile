package com.example.tiketbantu.ui.screens.mytickets

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.FilterPill
import com.example.tiketbantu.ui.components.TicketCard
import com.example.tiketbantu.ui.screens.feed.EmptyState
import com.example.tiketbantu.ui.session.AppRole
import com.example.tiketbantu.ui.session.DemoSession
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * My Tickets screen: Displays personal submitted tickets (Pelapor) or assigned tasks (Agen/Admin).
 */
@Composable
fun MyTicketsScreen(
    onTicketClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository: TicketRepository = koinInject()
    val scope = rememberCoroutineScope()
    val role = DemoSession.role

    val ticketsFlow = remember(repository, role) {
        if (role == AppRole.AGEN) {
            repository.getAllTickets(query = "", categoryId = null, status = null, sortByMostLiked = false, currentUserId = 20L, limit = 100)
        } else {
            repository.getMyTickets(userId = 1L)
        }
    }
    val allTickets by ticketsFlow.collectAsState(initial = emptyList())

    val statusFilters = listOf("Semua", TicketStatus.DIPROSES, TicketStatus.BARU, TicketStatus.SELESAI)
    var selectedStatus by remember { mutableStateOf("Semua") }

    val filteredTickets = remember(allTickets, selectedStatus, role) {
        val base = if (role == AppRole.AGEN) {
            allTickets.filter { it.agentId == 20L || it.agentName?.contains("Joko") == true || it.status == TicketStatus.DIPROSES }
        } else {
            allTickets
        }
        if (selectedStatus == "Semua") base
        else base.filter { it.status.equals(selectedStatus, ignoreCase = true) }
    }

    AppBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
            ) {
                Text(
                    text = if (role == AppRole.AGEN) "Tugas Penanganan" else "Aduan Saya",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 26.sp),
                    color = Ink
                )
                Text(
                    text = if (role == AppRole.AGEN) "Daftar tiket sarpras yang ditugaskan ke Anda" else "Pantau status aduan fasilitas yang Anda kirim",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted
                )
            }

            // Status filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusFilters.forEach { status ->
                    val label = when (status) {
                        "Semua" -> "Semua"
                        TicketStatus.BARU -> "Baru"
                        TicketStatus.DIPROSES -> "Diproses"
                        TicketStatus.SELESAI -> "Selesai"
                        else -> status
                    }
                    FilterPill(
                        text = label,
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = status }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (filteredTickets.isEmpty()) {
                EmptyState(
                    title = "Belum Ada Aduan",
                    message = if (role == AppRole.AGEN) "Belum ada tiket penanganan untuk filter ini." else "Anda belum membuat aduan pada kategori ini.",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredTickets, key = { it.id }) { ticket ->
                        TicketCard(
                            ticket = ticket,
                            onClick = { onTicketClick(ticket.id) },
                            onToggleSupport = {
                                scope.launch {
                                    repository.toggleSupport(ticket.id, 1L)
                                }
                            },
                            role = role
                        )
                    }
                }
            }
        }
    }
}
