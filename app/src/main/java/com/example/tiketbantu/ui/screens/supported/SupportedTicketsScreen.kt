package com.example.tiketbantu.ui.screens.supported

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
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
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.FilterPill
import com.example.tiketbantu.ui.components.TagChip
import com.example.tiketbantu.ui.components.TicketCard
import com.example.tiketbantu.ui.screens.feed.EmptyState
import com.example.tiketbantu.ui.session.DemoSession
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.SupportOrange
import com.example.tiketbantu.ui.theme.SupportOrangeSoft
import com.example.tiketbantu.ui.theme.SupportOrangeText
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Screen displaying campus tickets that the user has supported ("Saya Juga Mengalami").
 */
@Composable
fun SupportedTicketsScreen(
    onTicketClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository: TicketRepository = koinInject()
    val scope = rememberCoroutineScope()

    val ticketsFlow = remember(repository) {
        repository.getAllTickets(
            query = "",
            categoryId = null,
            status = null,
            sortByMostLiked = false,
            currentUserId = 1L,
            limit = 100
        )
    }
    val allTickets by ticketsFlow.collectAsState(initial = emptyList())

    val statusFilters = listOf("Semua", TicketStatus.DIPROSES, TicketStatus.BARU, TicketStatus.SELESAI)
    var selectedStatus by remember { mutableStateOf("Semua") }

    val supportedTickets = remember(allTickets, selectedStatus) {
        // Only tickets marked as supported by the current user
        val userSupported = allTickets.filter { it.isSupportedByMe }
        if (selectedStatus == "Semua") userSupported
        else userSupported.filter { it.status.equals(selectedStatus, ignoreCase = true) }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dukungan Saya",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 26.sp),
                        color = Ink,
                        modifier = Modifier.weight(1f)
                    )
                    TagChip(
                        text = "${supportedTickets.size} Didukung",
                        icon = Icons.Default.LocalFireDepartment,
                        container = SupportOrangeSoft,
                        content = SupportOrangeText
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Aduan sarpras yang Anda dukung dengan 'Saya Juga Mengalami'",
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

            if (supportedTickets.isEmpty()) {
                EmptyState(
                    title = "Belum Ada Dukungan",
                    message = "Anda belum mendukung aduan fasilitas apapun. Buka Beranda dan klik 'Saya Juga Mengalami' untuk mempercepat penanganan fasilitas kampus.",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(supportedTickets, key = { it.id }) { ticket ->
                        TicketCard(
                            ticket = ticket,
                            onClick = { onTicketClick(ticket.id) },
                            onToggleSupport = {
                                scope.launch {
                                    repository.toggleSupport(ticket.id, 1L)
                                }
                            },
                            role = DemoSession.role
                        )
                    }
                }
            }
        }
    }
}
