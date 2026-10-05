package com.example.tiketbantu.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tiketbantu.base.UiState
import com.example.tiketbantu.data.local.dao.CategoryDao
import com.example.tiketbantu.domain.model.Comment
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.AppInput
import com.example.tiketbantu.ui.components.AvatarStack
import com.example.tiketbantu.ui.components.CategoryBadge
import com.example.tiketbantu.ui.components.CircleIconButton
import com.example.tiketbantu.ui.components.FieldLabel
import com.example.tiketbantu.ui.components.FilterPill
import com.example.tiketbantu.ui.components.getCategoryStyle
import com.example.tiketbantu.ui.components.GlassCard
import com.example.tiketbantu.ui.components.GradientButton
import com.example.tiketbantu.ui.components.InfoCountPill
import com.example.tiketbantu.ui.components.InitialsAvatar
import com.example.tiketbantu.ui.components.StatusPill
import com.example.tiketbantu.ui.components.SupportPill
import com.example.tiketbantu.ui.components.TagChip
import com.example.tiketbantu.ui.components.locationText
import com.example.tiketbantu.ui.components.relativeTime
import com.example.tiketbantu.ui.components.ticketCode
import com.example.tiketbantu.ui.session.AppRole
import com.example.tiketbantu.ui.session.DemoSession
import com.example.tiketbantu.ui.theme.BrandCyan
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.BrandIndigoSoft
import com.example.tiketbantu.ui.theme.DangerRed
import com.example.tiketbantu.ui.theme.FieldBg
import com.example.tiketbantu.ui.theme.Hairline
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import com.example.tiketbantu.ui.theme.SuccessSoftBg
import com.example.tiketbantu.ui.theme.SuccessText
import com.example.tiketbantu.ui.theme.SupportOrange
import com.example.tiketbantu.ui.theme.SupportOrangeSoft
import com.example.tiketbantu.ui.theme.SupportOrangeText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Comment thread refresh interval (APP_FLOW alur 7: poll every ~5 seconds). */
private const val COMMENT_POLL_INTERVAL_MS = 5_000L

private val supporterNames = listOf("Rina Kartika", "Dimas Putra", "Ayu Lestari", "Bagas Wira", "Nadia Safitri")

internal fun imageModelOf(path: String): Any =
    if (path.startsWith("content:") || path.startsWith("file:") || path.startsWith("http")) path else File(path)

/**
 * Ticket detail screen (stateful wrapper). Hook it into the NavGraph.
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
    val repository: TicketRepository = koinInject()
    val categoryDao: CategoryDao = koinInject()
    val categories by categoryDao.getAllCategories().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var pendingScrollToEnd by remember { mutableStateOf(false) }
    var pendingStatus by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }

    val activeRole = when (currentUser?.role?.uppercase()) {
        "ADMIN" -> AppRole.ADMIN
        "AGEN" -> AppRole.AGEN
        else -> DemoSession.role
    }
    val role = activeRole
    val canAct = canUpdateStatus || role == AppRole.AGEN || role == AppRole.ADMIN
    var zoomedPhotoPath by rememberSaveable { mutableStateOf<String?>(null) }

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

    fun toast(msg: String) = scope.launch {
        snackbarHostState.currentSnackbarData?.dismiss()
        snackbarHostState.showSnackbar(msg)
    }

    AppBackground(modifier = modifier) {
        Column(Modifier.fillMaxSize()) {
            val ticket = (ticketState as? UiState.Success)?.data
            DetailTopBar(
                ticket = ticket,
                role = activeRole,
                currentUserId = currentUser?.id,
                onBack = onBack,
                onEdit = { showEditDialog = true },
                onDelete = { confirmDelete = true }
            )

            Box(Modifier.weight(1f)) {
                when (val state = ticketState) {
                    UiState.Idle, UiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator(color = BrandIndigo)
                    }

                    is UiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(state.message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = InkSoft)
                            GradientButton(text = "Coba Lagi", onClick = viewModel::retry, height = 46.dp, modifier = Modifier.width(180.dp))
                        }
                    }

                    is UiState.Success -> DetailList(
                        ticket = state.data,
                        comments = comments,
                        currentUserId = currentUser?.id,
                        role = activeRole,
                        canAct = canAct,
                        listState = listState,
                        onToggleSupport = viewModel::onToggleSupport,
                        onRequestStatusChange = { pendingStatus = it },
                        onZoomPhoto = { path -> zoomedPhotoPath = path }
                    )
                }
            }

            if (ticketState is UiState.Success) {
                val ticket = (ticketState as UiState.Success).data
                if (TicketStatus.isFinished(ticket.status)) {
                    LockedCommentBar(status = ticket.status)
                } else {
                    CommentInputBar(
                        value = commentInput,
                        isSending = isSending,
                        onValueChange = viewModel::onCommentInputChange,
                        onSend = viewModel::sendComment
                    )
                }
            }
        }

        SnackbarHost(
            snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 165.dp)
        )
    }

    pendingStatus?.let { status ->
        val (dialogTitle, dialogText, confirmBtnText) = when (status) {
            TicketStatus.DIPROSES -> Triple(
                "Konfirmasi Klaim Tugas",
                "Tiket ini akan ditugaskan ke Anda dan statusnya langsung berubah menjadi 'Diproses'.",
                "Ya, Klaim Tugas"
            )
            TicketStatus.SELESAI -> Triple(
                "Tandai Aduan Selesai?",
                "Penanganan fasilitas telah tuntas. Tiket akan terkunci dan dipindah ke arsip selesai.",
                "Ya, Selesai"
            )
            else -> Triple(
                "Tutup Tiket Aduan?",
                "Tiket akan ditutup dan terkunci.",
                "Ya, Tutup Tiket"
            )
        }
        AlertDialog(
            onDismissRequest = { pendingStatus = null },
            containerColor = Color.White,
            title = { Text(dialogTitle, fontWeight = FontWeight.Bold) },
            text = { Text(dialogText) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateStatus(status)
                    pendingStatus = null
                }) { Text(confirmBtnText, color = BrandIndigo, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { pendingStatus = null }) { Text("Batal", color = InkSoft) }
            }
        )
    }

    if (showEditDialog) {
        val currentTicket = (ticketState as? UiState.Success)?.data
        if (currentTicket != null) {
            EditTicketDialog(
                ticket = currentTicket,
                categories = categories,
                onDismiss = { showEditDialog = false },
                onSave = { newTitle, newDesc, newCatId ->
                    viewModel.updateTicketContent(newTitle, newDesc, newCatId)
                    showEditDialog = false
                }
            )
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = { Text("Hapus Aduan?", fontWeight = FontWeight.Bold) },
            text = { Text("Yakin ingin menghapus aduan ini?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    scope.launch {
                        runCatching { repository.softDeleteTicket(ticketId) }
                        onBack()
                    }
                }) { Text("Yakin", color = DangerRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Tidak", color = InkSoft) }
            }
        )
    }

    zoomedPhotoPath?.let { path ->
        ZoomablePhotoDialog(imagePath = path, onDismiss = { zoomedPhotoPath = null })
    }
}

// ─────────────────────────────────────────────────────────────
// Top bar
// ─────────────────────────────────────────────────────────────

@Composable
private fun DetailTopBar(
    ticket: Ticket?,
    role: AppRole,
    currentUserId: Long?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    val activeUserId = currentUserId ?: DemoSession.userId
    val isOwner = ticket != null && ticket.reporterId == activeUserId
    val canEdit = ticket?.status == TicketStatus.BARU && isOwner
    val canDelete = isOwner || role == AppRole.ADMIN

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = ticket?.let { ticketCode(it.id) } ?: "Memuat…",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 21.sp),
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (ticket != null) {
                    Spacer(Modifier.width(8.dp))
                    val resolvedCategory = ticket.categoryName.ifBlank {
                        when (ticket.categoryId) {
                            1L -> "Jaringan"
                            2L -> "Hardware"
                            3L -> "Software"
                            4L -> "Fasilitas"
                            else -> "Umum"
                        }
                    }
                    CategoryBadge(categoryName = resolvedCategory)
                }
            }
            Text(
                text = if (role == AppRole.AGEN) "Tugas Penanganan Fasilitas" else "Detail Aduan Fasilitas",
                style = MaterialTheme.typography.bodySmall,
                color = InkMuted
            )
        }
        if (canEdit || canDelete) {
            Box {
                CircleIconButton(Icons.Filled.MoreVert, "Lainnya", { menuOpen = true }, bordered = false, container = Color.Transparent)
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }, containerColor = Color.White) {
                    if (canEdit) {
                        DropdownMenuItem(
                            text = { Text("Edit Aduan") },
                            leadingIcon = { Icon(Icons.Outlined.Edit, null) },
                            onClick = { menuOpen = false; onEdit() }
                        )
                    }
                    if (canDelete) {
                        DropdownMenuItem(
                            text = { Text("Hapus Aduan", color = DangerRed) },
                            leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = DangerRed) },
                            onClick = { menuOpen = false; onDelete() }
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Body
// ─────────────────────────────────────────────────────────────

@Composable
private fun DetailList(
    ticket: Ticket,
    comments: List<Comment>,
    currentUserId: Long?,
    role: AppRole,
    canAct: Boolean,
    listState: LazyListState,
    onToggleSupport: () -> Unit,
    onRequestStatusChange: (String) -> Unit,
    onZoomPhoto: (String) -> Unit
) {
    val finished = TicketStatus.isFinished(ticket.status)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "status") { StatusCard(ticket, role) }
        item(key = "main") { MainInfoCard(ticket, onZoomPhoto) }
        item(key = "support") { SupportCard(ticket, role, onToggleSupport) }

        val isStaff = role == AppRole.AGEN || role == AppRole.ADMIN

        if (finished) {
            item(key = "locked") {
                GlassCard(containerColor = SuccessSoftBg, borderColor = SuccessSoftBg) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (ticket.status == TicketStatus.SELESAI) Icons.Outlined.CheckCircle else Icons.Outlined.Lock,
                            null, tint = SuccessText, modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Aduan ini sudah ${if (ticket.status == TicketStatus.SELESAI) "dituntaskan" else "ditutup"} dan terkunci.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = SuccessText
                        )
                    }
                }
            }
        } else if (isStaff && canAct) {
            item(key = "actions") { ActionPanel(ticket, onRequestStatusChange) }
        }

        item(key = "thread-header") {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Text("Thread Penanganan", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold), color = Ink)
                Spacer(Modifier.width(8.dp))
                TagChip("${comments.size} Komentar", container = BrandIndigoSoft, content = BrandIndigo)
                Spacer(Modifier.weight(1f))
                TagChip("Live Stream", container = SuccessSoftBg, content = SuccessText)
            }
        }

        if (comments.isEmpty()) {
            item(key = "empty") {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Belum ada tanggapan. Jadilah yang pertama menanggapi.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            items(comments, key = { "c-${it.id}" }) { comment ->
                CommentCard(comment, isMine = comment.userId == currentUserId)
            }
        }
    }
}

@Composable
private fun StatusCard(ticket: Ticket, role: AppRole) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    "STATUS TIKET",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = InkMuted
                )
                Spacer(Modifier.height(6.dp))
                StatusPill(ticket.status)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccessTime, null, tint = InkMuted, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(4.dp))
                Text("Diperbarui ${relativeTime(ticket.updatedAt)}", style = MaterialTheme.typography.labelSmall, color = InkSoft)
            }
        }
        if (role == AppRole.AGEN || role == AppRole.ADMIN) {
            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))
            Spacer(Modifier.height(14.dp))

            val assignee = ticket.agentName
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (assignee != null) {
                    InitialsAvatar(assignee, size = 44.dp, soft = false)
                } else {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(FieldBg),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Outlined.Engineering, null, tint = InkMuted) }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Ditugaskan Kepada:", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                    Text(
                        assignee ?: "Menunggu diklaim teknisi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Ink
                    )
                    Text("Divisi Teknisi Sarana & Prasarana", style = MaterialTheme.typography.bodySmall, color = InkSoft)
                }
            }
        }
    }
}

@Composable
private fun MainInfoCard(ticket: Ticket, onZoomPhoto: (String) -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            ticket.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, lineHeight = 26.sp),
            color = Ink
        )
        Spacer(Modifier.height(8.dp))
        Text(ticket.description, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp), color = InkSoft)
        Spacer(Modifier.height(14.dp))

        InfoTile(Icons.Outlined.Place, "Lokasi Spesifik", ticket.locationText().ifBlank { "-" })
        Spacer(Modifier.height(10.dp))
        InfoTile(
            Icons.Outlined.Person,
            "Pelapor Pertama",
            ticket.reporterName.ifBlank { "Civitas Kampus" },
            caption = "Dilaporkan: ${formatDate(ticket.createdAt)}"
        )

        if (!ticket.imageUrl.isNullOrBlank()) {
            val path = ticket.imageUrl
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.PhotoCamera, null, tint = Ink, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "Bukti Lampiran Lapangan (1)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                    modifier = Modifier.weight(1f)
                )
                Text("Format Terverifikasi", style = MaterialTheme.typography.labelSmall, color = InkMuted)
            }
            Spacer(Modifier.height(10.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(FieldBg)
                    .clickable { onZoomPhoto(path) }
            ) {
                AsyncImage(
                    model = imageModelOf(path),
                    contentDescription = "Foto bukti aduan",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))))
                )
                Text(
                    "Kondisi Lapangan Terkini (Ketuk untuk perbesar)",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.align(Alignment.BottomStart).padding(14.dp)
                )
                CircleIconButton(
                    Icons.Outlined.ZoomIn, "Perbesar", { onZoomPhoto(path) },
                    container = Color.White.copy(alpha = 0.9f), bordered = false, size = 38.dp,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoTile(icon: ImageVector, label: String, value: String, caption: String? = null) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Hairline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = BrandIndigo, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = InkMuted)
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (caption != null) Text(caption, style = MaterialTheme.typography.labelSmall, color = InkMuted)
            }
        }
    }
}

@Composable
private fun SupportCard(ticket: Ticket, role: AppRole, onToggleSupport: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Dukungan Aduan",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Ink
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (role == AppRole.AGEN || role == AppRole.ADMIN)
                        "Statistik jumlah pelapor yang mendukung penanganan fasilitas ini"
                    else "Dukungan Anda mempercepat prioritas penanganan sarpras",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted
                )
            }
            Spacer(Modifier.width(12.dp))
            if (role == AppRole.AGEN || role == AppRole.ADMIN) {
                InfoCountPill(
                    icon = Icons.Outlined.FavoriteBorder,
                    count = ticket.supportCount,
                    contentDescription = "Jumlah dukungan"
                )
            } else {
                SupportPill(
                    count = ticket.supportCount,
                    supported = ticket.isSupportedByMe,
                    onClick = onToggleSupport,
                    enabled = !TicketStatus.isFinished(ticket.status)
                )
            }
        }
    }
}

@Composable
private fun ActionPanel(
    ticket: Ticket,
    onRequestStatusChange: (String) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Engineering, null, tint = BrandIndigo, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                "PANEL TINDAKAN TEKNISI SARPRAS",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp),
                color = Ink,
                modifier = Modifier.weight(1f)
            )
            Text("Akses Petugas", style = MaterialTheme.typography.labelSmall, color = InkMuted)
        }
        Spacer(Modifier.height(12.dp))
        if (ticket.status == TicketStatus.BARU) {
            Surface(
                onClick = { onRequestStatusChange(TicketStatus.DIPROSES) },
                shape = RoundedCornerShape(50),
                color = BrandIndigo,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Bolt, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Klaim Tugas Ini", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    onClick = { onRequestStatusChange(TicketStatus.SELESAI) },
                    shape = RoundedCornerShape(50),
                    color = BrandIndigo,
                    modifier = Modifier.weight(1f).height(46.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CheckCircle, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Tandai Selesai", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(
                    onClick = { onRequestStatusChange(TicketStatus.DITUTUP) },
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    border = BorderStroke(1.dp, Hairline),
                    modifier = Modifier.weight(1f).height(46.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.HighlightOff, null, tint = Ink, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Tutup Tiket", color = Ink, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditTicketDialog(
    ticket: Ticket,
    categories: List<com.example.tiketbantu.data.local.entity.CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (String, String, Long) -> Unit
) {
    var title by rememberSaveable { mutableStateOf(ticket.title) }
    var description by rememberSaveable { mutableStateOf(ticket.description) }
    var selectedCategoryId by rememberSaveable { mutableStateOf(ticket.categoryId) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Text("Edit Aduan Kampus", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    FieldLabel(text = "Judul Masalah", required = true)
                    Spacer(Modifier.height(4.dp))
                    AppInput(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "Ringkasan kerusakan...",
                        singleLine = true
                    )
                }

                Column {
                    FieldLabel(text = "Kategori Permasalahan", required = true)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategoryId == cat.id
                            val style = getCategoryStyle(cat.name)
                            Surface(
                                onClick = { selectedCategoryId = cat.id },
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) style.bg else Color.Transparent,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) style.text else Hairline
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = style.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) style.text else InkMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = cat.name,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) style.text else InkSoft
                                    )
                                }
                            }
                        }
                    }
                }

                Column {
                    FieldLabel(text = "Deskripsi Lengkap", required = true)
                    Spacer(Modifier.height(4.dp))
                    AppInput(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "Jelaskan permasalahan fasilitas secara detail...",
                        minLines = 3,
                        singleLine = false
                    )
                }

                if (errorText != null) {
                    Text(errorText ?: "", color = DangerRed, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isBlank() || description.isBlank()) {
                    errorText = "Judul dan deskripsi tidak boleh kosong."
                    return@TextButton
                }
                onSave(title.trim(), description.trim(), selectedCategoryId)
            }) {
                Text("Simpan", color = BrandIndigo, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = InkSoft)
            }
        }
    )
}

private fun roleChip(role: String): Triple<String, Color, Color> = when (role.uppercase()) {
    "AGEN" -> Triple("Teknisi Resmi", BrandIndigoSoft, BrandIndigo)
    "ADMIN" -> Triple("Admin", Color(0xFFF3E8FF), Color(0xFF7E22CE))
    "PELAPOR_UTAMA" -> Triple("Pelapor", Color(0xFFDDF4FF), Color(0xFF0369A1))
    else -> Triple("Mahasiswa", FieldBg, InkSoft)
}

@Composable
private fun CommentCard(comment: Comment, isMine: Boolean) {
    val isAgent = comment.userRole.equals("AGEN", ignoreCase = true)
    val (label, chipBg, chipFg) = roleChip(comment.userRole)
    val name = comment.userName.ifBlank { "Pengguna" }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isAgent) Color(0xFFF7F8FF) else Color.White,
        border = BorderStroke(1.dp, if (isAgent) BrandIndigo.copy(alpha = 0.18f) else Hairline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            if (isAgent) {
                Box(Modifier.width(4.dp).fillMaxHeight().background(BrandIndigo))
            }
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InitialsAvatar(name, size = 30.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(6.dp))
                    TagChip(label, container = chipBg, content = chipFg, fontSize = 10.sp)
                    Spacer(Modifier.weight(1f))
                    Text(formatTime(comment.createdAt), style = MaterialTheme.typography.labelSmall, color = InkMuted)
                }
                Spacer(Modifier.height(8.dp))
                Text(comment.content, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp), color = Ink)
            }
        }
    }
}

@Composable
private fun LockedCommentBar(status: String) {
    Surface(
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Outlined.Lock,
                contentDescription = null,
                tint = InkMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (status == TicketStatus.SELESAI) "Aduan telah dituntaskan. Diskusi komentar dikunci." else "Aduan telah ditutup. Diskusi komentar dikunci.",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = InkMuted
            )
        }
    }
}

@Composable
private fun CommentInputBar(
    value: String,
    isSending: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    val enabled = value.isNotBlank() && !isSending
    Surface(
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tulis tanggapan atau pembaruan…", color = InkMuted, style = MaterialTheme.typography.bodyMedium) },
                maxLines = 4,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Ink),
                shape = RoundedCornerShape(50),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { if (enabled) onSend() }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = FieldBg,
                    unfocusedContainerColor = FieldBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = BrandIndigo
                )
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (enabled) Brush.linearGradient(listOf(BrandIndigo, BrandCyan))
                        else Brush.linearGradient(listOf(Color(0xFFCBD5E1), Color(0xFFCBD5E1)))
                    )
                    .clickable(enabled = enabled, onClick = onSend),
                contentAlignment = Alignment.Center
            ) {
                if (isSending) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, "Kirim", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ZoomablePhotoDialog(imagePath: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.Black.copy(alpha = 0.94f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pratinjau Foto Lampiran", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Tutup", tint = Color.White)
                    }
                }
                Spacer(Modifier.height(12.dp))
                AsyncImage(
                    model = imageModelOf(imagePath),
                    contentDescription = "Foto bukti diperbesar",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID")).format(Date(millis)) + " WIB"

private fun formatTime(millis: Long): String =
    SimpleDateFormat("HH:mm", Locale.forLanguageTag("id-ID")).format(Date(millis)) + " WIB"

@Suppress("unused")
private val unusedIcons = listOf(Icons.Filled.Check)