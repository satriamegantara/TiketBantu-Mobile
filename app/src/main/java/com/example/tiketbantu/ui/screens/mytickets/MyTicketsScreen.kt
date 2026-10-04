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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.domain.repository.AuthRepository
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
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val repository: TicketRepository = koinInject()
    val authRepository: AuthRepository = koinInject()
    val scope = rememberCoroutineScope()
    val currentUser by authRepository.getCurrentUser().collectAsState(initial = null)
    val role = currentUser?.let {
        when (it.role.uppercase()) {
            "ADMIN" -> AppRole.ADMIN
            "AGEN" -> AppRole.AGEN
            else -> AppRole.PELAPOR
        }
    } ?: if (DemoSession.isLoggedIn) DemoSession.role else AppRole.PELAPOR

    val currentUserId = currentUser?.id ?: DemoSession.userId

    val ticketsFlow = remember(repository, role, currentUserId) {
        if (role == AppRole.AGEN) {
            repository.getAllTickets(query = "", categoryId = null, status = null, sortByMostLiked = false, currentUserId = currentUserId, limit = 100)
        } else {
            repository.getMyTickets(userId = currentUserId)
        }
    }
    val allTickets by ticketsFlow.collectAsState(initial = emptyList())

    val statusFilters = listOf("Semua", TicketStatus.DIPROSES, TicketStatus.BARU, TicketStatus.SELESAI)
    val initial = remember { DemoSession.myTicketsInitialStatus.also { DemoSession.myTicketsInitialStatus = null } }
    var selectedStatus by remember { mutableStateOf(initial ?: "Semua") }

    val filteredTickets = remember(allTickets, selectedStatus, role, currentUserId) {
        val base = if (role == AppRole.AGEN) {
            allTickets.filter {
                it.agentId == currentUserId ||
                (it.status == TicketStatus.BARU && it.agentId == null)
            }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (role == AppRole.AGEN) "Tugas Penanganan" else "Aduan Saya",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 26.sp),
                        color = Ink
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (role == AppRole.AGEN) "Daftar tugas penanganan sarpras yang ditugaskan kepada Anda" else "Pantau status aduan fasilitas yang Anda kirim",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkMuted
                    )
                }
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
                        "Semua" -> if (role == AppRole.AGEN) "Semua Tugas" else "Semua"
                        TicketStatus.BARU -> if (role == AppRole.AGEN) "Tugas Baru" else "Baru"
                        TicketStatus.DIPROSES -> if (role == AppRole.AGEN) "Penanganan" else "Diproses"
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
                val emptyTitle = if (role == AppRole.AGEN) "Belum Ada Tugas" else "Belum Ada Aduan"
                val emptyMessage = if (role == AppRole.AGEN) {
                    if (selectedStatus == "Semua") {
                        "Belum ada tugas penanganan yang ditugaskan kepada Anda."
                    } else {
                        val statusLabel = when (selectedStatus) {
                            TicketStatus.BARU -> "tugas baru"
                            TicketStatus.DIPROSES -> "penanganan"
                            TicketStatus.SELESAI -> "tugas selesai"
                            else -> selectedStatus.lowercase()
                        }
                        "Tidak ada tugas penanganan dengan status $statusLabel."
                    }
                } else {
                    if (selectedStatus == "Semua") {
                        "Anda belum membuat aduan fasilitas kampus."
                    } else {
                        "Tidak ada aduan dengan status ${selectedStatus.lowercase()}."
                    }
                }
                EmptyState(
                    title = emptyTitle,
                    message = emptyMessage,
                    icon = if (role == AppRole.AGEN) Icons.AutoMirrored.Outlined.Assignment else Icons.Outlined.Inbox,
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
                                    repository.toggleSupport(ticket.id, currentUserId)
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
