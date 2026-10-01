package com.example.tiketbantu.ui.screens.detail

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tiketbantu.base.UiState
import com.example.tiketbantu.domain.model.Comment
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.ui.screens.feed.StatusBadge
import com.example.tiketbantu.ui.screens.feed.SupportButton
import com.example.tiketbantu.ui.theme.StatusBadgeBaru
import com.example.tiketbantu.ui.theme.StatusBadgeDiproses
import com.example.tiketbantu.ui.theme.StatusBadgeDitutup
import com.example.tiketbantu.ui.theme.StatusBadgeSelesai
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.io.File

/** Comment thread refresh interval (APP_FLOW alur 7: poll every ~5 seconds). */
private const val COMMENT_POLL_INTERVAL_MS = 5_000L

/**
 * Ticket detail screen (stateful wrapper). Hook it into the NavGraph, e.g.
 *
 *   composable<Screen.TicketDetail> { entry ->
 *       val route = entry.toRoute<Screen.TicketDetail>()
 *       TicketDetailScreen(ticketId = route.ticketId, onBack = { navController.popBackStack() })
 *   }
 */
@Composable
fun TicketDetailScreen(
    ticketId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TicketDetailViewModel = koinViewModel(parameters = { parametersOf(ticketId) })
) {
    val ticketState by viewModel.ticketState.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val commentInput by viewModel.commentInput.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val canUpdateStatus by viewModel.canUpdateStatus.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var pendingScrollToEnd by remember { mutableStateOf(false) }
    var pendingStatus by rememberSaveable { mutableStateOf<String?>(null) }

    // 3.5 polling: refresh the thread every 5 seconds while this screen is in composition.
    LaunchedEffect(viewModel) {
        while (true) {
            delay(COMMENT_POLL_INTERVAL_MS)
            viewModel.refreshComments()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is DetailEvent.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(event.message)
                }
                DetailEvent.CommentSent -> pendingScrollToEnd = true
            }
        }
    }

    // Scroll to the newest comment right after the user posted one (not on every poll).
    LaunchedEffect(pendingScrollToEnd, comments.size) {
        if (pendingScrollToEnd) {
            val total = listState.layoutInfo.totalItemsCount
            if (total > 0) listState.animateScrollToItem(total - 1)
            pendingScrollToEnd = false
        }
    }

    TicketDetailContent(
        ticketState = ticketState,
        comments = comments,
        commentInput = commentInput,
        isSending = isSending,
        currentUserId = currentUser?.id,
        canUpdateStatus = canUpdateStatus,
        snackbarHostState = snackbarHostState,
        listState = listState,
        onBack = onBack,
        onRetry = viewModel::retry,
        onCommentInputChange = viewModel::onCommentInputChange,
        onSendComment = viewModel::sendComment,
        onToggleSupport = viewModel::onToggleSupport,
        onRequestStatusChange = { status -> pendingStatus = status },
        modifier = modifier
    )

    pendingStatus?.let { status ->
        val label = if (status == TicketStatus.SELESAI) "Selesai" else "Ditutup"
        AlertDialog(
            onDismissRequest = { pendingStatus = null },
            title = { Text("Ubah status aduan?") },
            text = { Text("Status akan menjadi \"$label\". Setelah itu aduan terkunci dan tidak dapat diubah lagi.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateStatus(status)
                    pendingStatus = null
                }) { Text("Ya, ubah") }
            },
            dismissButton = {
                TextButton(onClick = { pendingStatus = null }) { Text("Batal") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailContent(
    ticketState: UiState<Ticket>,
    comments: List<Comment>,
    commentInput: String,
    isSending: Boolean,
    currentUserId: Long?,
    canUpdateStatus: Boolean,
    snackbarHostState: SnackbarHostState,
    listState: LazyListState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onCommentInputChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onToggleSupport: () -> Unit,
    onRequestStatusChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detail Aduan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (ticketState is UiState.Success) {
                CommentInputBar(
                    value = commentInput,
                    isSending = isSending,
                    onValueChange = onCommentInputChange,
                    onSend = onSendComment
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            when (ticketState) {
                UiState.Idle, UiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = ticketState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = onRetry) { Text("Coba Lagi") }
                    }
                }

                is UiState.Success -> DetailList(
                    ticket = ticketState.data,
                    comments = comments,
                    currentUserId = currentUserId,
                    canUpdateStatus = canUpdateStatus,
                    listState = listState,
                    onToggleSupport = onToggleSupport,
                    onRequestStatusChange = onRequestStatusChange
                )
            }
        }
    }
}

@Composable
private fun DetailList(
    ticket: Ticket,
    comments: List<Comment>,
    currentUserId: Long?,
    canUpdateStatus: Boolean,
    listState: LazyListState,
    onToggleSupport: () -> Unit,
    onRequestStatusChange: (String) -> Unit
) {
    val finished = TicketStatus.isFinished(ticket.status)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "header") { TicketHeader(ticket) }
        item(key = "info") { TicketInfoCard(ticket) }

        ticket.imageUrl?.takeIf { it.isNotBlank() }?.let { path ->
            item(key = "photo") {
                AsyncImage(
                    model = File(path),
                    contentDescription = "Foto bukti aduan",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }
        }

        item(key = "support") {
            SupportButton(
                supported = ticket.isSupportedByMe,
                count = ticket.supportCount,
                onClick = onToggleSupport
            )
        }

        if (finished) {
            item(key = "locked") {
                Text(
                    text = "Aduan ini sudah ${if (ticket.status == TicketStatus.SELESAI) "selesai" else "ditutup"} dan terkunci.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (canUpdateStatus) {
            item(key = "status-actions") { StatusActionsCard(onRequestStatusChange) }
        }

        item(key = "history") { StatusHistory(ticket) }

        item(key = "comments-header") {
            Text(
                text = "Komentar (${comments.size})",
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (comments.isEmpty()) {
            item(key = "comments-empty") {
                Text(
                    text = "Belum ada komentar. Jadilah yang pertama menanggapi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(comments, key = { "comment-${it.id}" }) { comment ->
                CommentBubble(comment = comment, isMine = comment.userId == currentUserId)
            }
        }
    }
}

@Composable
private fun TicketHeader(ticket: Ticket) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StatusBadge(status = ticket.status)
        Text(text = ticket.title, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = ticket.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TicketInfoCard(ticket: Ticket) {
    val location = listOf(ticket.locationBuilding, ticket.locationFloor, ticket.locationRoom)
        .filter { it.isNotBlank() }
        .joinToString(", ")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoRow("Pelapor", ticket.reporterName.ifBlank { "-" })
            InfoRow("Kategori", ticket.categoryName.ifBlank { "-" })
            InfoRow("Lokasi", location.ifBlank { "-" })
            InfoRow("Dibuat", rememberDateTime(ticket.createdAt))
            InfoRow("Penanggung jawab", ticket.agentName ?: "Belum diambil Agen")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun StatusActionsCard(onRequestStatusChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Perbarui status", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Setelah Selesai atau Ditutup, aduan terkunci dan tidak dapat diubah lagi.",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onRequestStatusChange(TicketStatus.SELESAI) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                ) { Text("Tandai Selesai") }
                OutlinedButton(
                    onClick = { onRequestStatusChange(TicketStatus.DITUTUP) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                ) { Text("Tutup Aduan") }
            }
        }
    }
}

private data class HistoryStep(val label: String, val timeMillis: Long?, val color: Color)

/**
 * Status history derived from the ticket itself (there is no history table in the schema):
 * created -> processed by agent -> finished/closed. The time of the "Diproses" step is only known
 * while the ticket is still DIPROSES (updatedAt); afterwards it is shown without a time.
 */
@Composable
private fun StatusHistory(ticket: Ticket) {
    val steps = buildList {
        add(HistoryStep("Aduan dibuat oleh ${ticket.reporterName.ifBlank { "pelapor" }}", ticket.createdAt, StatusBadgeBaru))
        if (ticket.agentId != null) {
            add(
                HistoryStep(
                    "Diproses oleh ${ticket.agentName ?: "Agen"}",
                    if (ticket.status == TicketStatus.DIPROSES) ticket.updatedAt else null,
                    StatusBadgeDiproses
                )
            )
        }
        when (ticket.status) {
            TicketStatus.SELESAI -> add(HistoryStep("Aduan selesai", ticket.updatedAt, StatusBadgeSelesai))
            TicketStatus.DITUTUP -> add(HistoryStep("Aduan ditutup", ticket.updatedAt, StatusBadgeDitutup))
            else -> Unit
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Riwayat status", style = MaterialTheme.typography.titleMedium)
        steps.forEach { step ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(10.dp)
                        .background(step.color, CircleShape)
                )
                Column {
                    Text(step.label, style = MaterialTheme.typography.bodyMedium)
                    step.timeMillis?.let { millis ->
                        Text(
                            text = rememberDateTime(millis),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentBubble(comment: Comment, isMine: Boolean) {
    val roleLabel = when (comment.userRole) {
        "AGEN" -> "Agen"
        "ADMIN" -> "Admin"
        else -> null
    }
    val timeAgo = remember(comment.createdAt) {
        DateUtils.getRelativeTimeSpanString(
            comment.createdAt,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isMine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.userName.ifBlank { "Pengguna" },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (roleLabel != null) {
                        Text(
                            text = roleLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(text = comment.content, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text(
            text = timeAgo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CommentInputBar(
    value: String,
    isSending: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(tonalElevation = 3.dp, modifier = Modifier.imePadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tulis komentar") },
                maxLines = 4,
                shape = MaterialTheme.shapes.large
            )
            Button(
                onClick = onSend,
                enabled = value.isNotBlank() && !isSending,
                modifier = Modifier.heightIn(min = 48.dp)
            ) { Text("Kirim") }
        }
    }
}

@Composable
private fun rememberDateTime(millis: Long): String {
    val context = LocalContext.current
    return remember(millis) {
        DateUtils.formatDateTime(
            context,
            millis,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME or
                DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_MONTH
        )
    }
}
