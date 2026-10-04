package com.example.tiketbantu.ui.screens.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.base.UiState
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.domain.repository.AuthRepository
import com.example.tiketbantu.domain.repository.CommentRepository
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.ui.components.AdaptiveFeedLayout
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.AppInput
import com.example.tiketbantu.ui.components.FeedSidebar
import com.example.tiketbantu.ui.components.FilterPill
import com.example.tiketbantu.ui.components.GlassCard
import com.example.tiketbantu.ui.components.GradientButton
import com.example.tiketbantu.ui.components.HeroSummaryCard
import com.example.tiketbantu.ui.components.SectionTitle
import com.example.tiketbantu.ui.components.TicketCard
import com.example.tiketbantu.ui.components.TicketCardSkeleton
import com.example.tiketbantu.ui.components.UserGreetingHeader
import com.example.tiketbantu.ui.components.ticketCode
import com.example.tiketbantu.ui.session.AppRole
import com.example.tiketbantu.ui.session.DemoSession
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.BrandIndigoSoft
import com.example.tiketbantu.ui.theme.DangerRed
import com.example.tiketbantu.ui.theme.FieldBg
import com.example.tiketbantu.ui.theme.Hairline
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import com.example.tiketbantu.ui.theme.SuccessText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/** Category ids follow the seed: 1 = IT, 2 = Ruangan, 3 = Umum (feature 3.2.2). */
private val CATEGORY_FILTERS = listOf(
    1L to "Teknologi & IT",
    2L to "Fasilitas Ruangan",
    3L to "Infrastruktur Umum"
)

private val STATUS_FILTERS: List<Pair<String?, String>> = listOf(
    null to "Semua Status",
    TicketStatus.BARU to "Baru",
    TicketStatus.DIPROSES to "Diproses",
    TicketStatus.SELESAI to "Selesai",
    TicketStatus.DITUTUP to "Ditutup"
)

/**
 * Beranda / public feed (features 2.2, 2.5, 2.6, 2.7, 3.3, 4.1–4.6).
 * Redesigned Filter UX: Clean Search + Filter trigger, M3 Modal BottomSheet, & compact active chips.
 */
@Composable
fun FeedScreen(
    onTicketClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    viewModel: FeedViewModel = koinViewModel()
) {
    val feedState by viewModel.feedState.collectAsState()
    val searchInput by viewModel.searchInput.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val effectiveSort by viewModel.effectiveSort.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val canLoadMore by viewModel.canLoadMore.collectAsState()

    val ticketRepository = koinInject<TicketRepository>()
    val commentRepository = koinInject<CommentRepository>()
    val authRepository = koinInject<AuthRepository>()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingClaimTicketId by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is FeedEvent.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    val currentUser by authRepository.getCurrentUser().collectAsState(initial = null)
    val activeRole = currentUser?.let {
        when (it.role.uppercase()) {
            "ADMIN" -> AppRole.ADMIN
            "AGEN" -> AppRole.AGEN
            else -> AppRole.PELAPOR
        }
    } ?: if (DemoSession.isLoggedIn) DemoSession.role else AppRole.PELAPOR
    val activeUserName = currentUser?.name ?: DemoSession.name

    AppBackground(modifier) {
        FeedContent(
            feedState = feedState,
            searchInput = searchInput,
            filter = filter,
            effectiveSort = effectiveSort,
            isRefreshing = isRefreshing,
            canLoadMore = canLoadMore,
            role = activeRole,
            userName = activeUserName,
            onResetFilters = viewModel::resetFilters,
            onSearchChange = viewModel::onSearchChange,
            onStatusSelected = viewModel::onStatusSelected,
            onCategorySelected = viewModel::onCategorySelected,
            onSortSelected = viewModel::onSortSelected,
            onRefresh = viewModel::refresh,
            onLoadMore = viewModel::loadMore,
            onRetry = viewModel::retry,
            onTicketClick = onTicketClick,
            onToggleSupport = viewModel::onToggleSupport,
            onProfileClick = onProfileClick,
            onClaim = { id ->
                scope.launch {
                    ticketRepository.updateTicketStatus(id, TicketStatus.DIPROSES, agentId = DemoSession.userId)
                    snackbarHostState.showSnackbar("Tiket ${ticketCode(id)} diklaim — status kini Diproses")
                }
            },
            commentCountOf = { id ->
                val count by produceState(0, id) {
                    value = runCatching { commentRepository.getComments(id).size }.getOrDefault(0)
                }
                count
            }
        )
        SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter).padding(bottom = 110.dp))
    }

    pendingClaimTicketId?.let { claimId ->
        AlertDialog(
            onDismissRequest = { pendingClaimTicketId = null },
            containerColor = Color.White,
            title = {
                Text("Konfirmasi Klaim Tugas", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Apakah Anda yakin ingin mengambil dan menangani aduan ${ticketCode(claimId)}? Status tiket akan berubah menjadi 'Diproses'.")
            },
            confirmButton = {
                TextButton(onClick = {
                    val idToClaim = claimId
                    pendingClaimTicketId = null
                    scope.launch {
                        ticketRepository.updateTicketStatus(idToClaim, TicketStatus.DIPROSES, agentId = DemoSession.userId)
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar("Tiket ${ticketCode(idToClaim)} berhasil diklaim — status kini Diproses")
                    }
                }) {
                    Text("Ya, Klaim", color = BrandIndigo, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingClaimTicketId = null }) {
                    Text("Batal", color = InkSoft)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedContent(
    feedState: UiState<List<Ticket>>,
    searchInput: String,
    filter: FeedFilter,
    effectiveSort: FeedSort,
    isRefreshing: Boolean,
    canLoadMore: Boolean,
    role: AppRole,
    userName: String,
    onResetFilters: () -> Unit,
    onSearchChange: (String) -> Unit,
    onStatusSelected: (String?) -> Unit,
    onCategorySelected: (Long?) -> Unit,
    onSortSelected: (FeedSort) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onTicketClick: (Long) -> Unit,
    onToggleSupport: (Long) -> Unit,
    onProfileClick: () -> Unit,
    onClaim: (Long) -> Unit,
    commentCountOf: @Composable (Long) -> Int,
    modifier: Modifier = Modifier
) {
    val tickets = (feedState as? UiState.Success)?.data ?: emptyList()
    val (active, finished) = remember(tickets) { tickets.partition { !TicketStatus.isFinished(it.status) } }
    val affected = remember(tickets) { tickets.sumOf { it.supportCount } }
    val done = remember(tickets) { tickets.count { it.status == TicketStatus.SELESAI } }

    var showFilterBottomSheet by remember { mutableStateOf(false) }

    val activeFilterCount = (if (filter.status != null) 1 else 0) +
            (if (filter.categoryId != null) 1 else 0) +
            (if (effectiveSort == FeedSort.MOST_LIKED) 1 else 0)

    val isFilterActive = activeFilterCount > 0 || searchInput.isNotBlank()

    val listState = rememberLazyListState()
    LoadMoreEffect(listState, canLoadMore, onLoadMore)

    AdaptiveFeedLayout(
        feedContent = {
            PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = onRefresh, modifier = Modifier.fillMaxSize()) {
                LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item(key = "header") {
                UserGreetingHeader(userName = userName, role = role, onAvatarClick = onProfileClick)
            }

            // Search Bar + Filter Trigger Button
            item(key = "search_and_filter") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppInput(
                        value = searchInput,
                        onValueChange = onSearchChange,
                        placeholder = "Cari aduan fasilitas, lab, kelas...",
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = InkMuted) },
                        trailingIcon = {
                            if (searchInput.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Default.Clear, "Hapus pencarian", tint = InkMuted)
                                }
                            }
                        }
                    )

                    // Compact Filter Trigger Button
                    Surface(
                        onClick = { showFilterBottomSheet = true },
                        shape = RoundedCornerShape(16.dp),
                        color = if (activeFilterCount > 0) BrandIndigoSoft else Color.White,
                        border = BorderStroke(1.dp, if (activeFilterCount > 0) BrandIndigo else Hairline),
                        shadowElevation = 1.dp,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.Tune,
                                contentDescription = "Filter",
                                tint = if (activeFilterCount > 0) BrandIndigo else InkMuted,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Compact Single Active Filter Indicator
            if (activeFilterCount > 0) {
                item(key = "active_filter_indicator") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = { showFilterBottomSheet = true },
                            shape = RoundedCornerShape(50),
                            color = BrandIndigoSoft,
                            border = BorderStroke(1.dp, BrandIndigo.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Tune, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Filter · $activeFilterCount aktif",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BrandIndigo
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(
                            onClick = onResetFilters,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Reset", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = DangerRed)
                        }
                    }
                }
            }

            item(key = "summary") {
                HeroSummaryCard(
                    openCount = active.size,
                    affectedCount = affected,
                    doneCount = done,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item(key = "feed-title") {
                SectionTitle(
                    title = "Feed Aduan Terkini",
                    icon = Icons.Outlined.RssFeed,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp),
                    trailing = {
                        Text("Pembaruan otomatis", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                    }
                )
            }

            when (feedState) {
                UiState.Idle, UiState.Loading -> items(3) {
                    TicketCardSkeleton(Modifier.padding(horizontal = 20.dp))
                }

                is UiState.Error -> item(key = "error") {
                    EmptyState(
                        title = "Gagal memuat feed",
                        message = feedState.message,
                        actionLabel = "Coba Lagi",
                        onAction = onRetry
                    )
                }

                is UiState.Success -> {
                    if (tickets.isEmpty()) {
                        item(key = "empty") {
                            EmptyState(
                                title = "Belum ada aduan",
                                message = if (searchInput.isNotBlank() || filter.status != null || filter.categoryId != null)
                                    "Tidak ada aduan yang cocok dengan pencarian atau filter."
                                else "Jadilah yang pertama melaporkan kendala fasilitas kampus."
                            )
                        }
                    }
                    items(active, key = { "t-${it.id}" }) { ticket ->
                        TicketCard(
                            ticket = ticket,
                            onClick = { onTicketClick(ticket.id) },
                            onToggleSupport = { onToggleSupport(ticket.id) },
                            role = role,
                            onClaimClick = { onClaim(ticket.id) },
                            commentCount = commentCountOf(ticket.id),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                    if (finished.isNotEmpty()) {
                        item(key = "finished-title") {
                            Text(
                                "Aduan Selesai & Ditutup",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = InkSoft,
                                modifier = Modifier.padding(start = 20.dp, top = 8.dp)
                            )
                        }
                        items(finished, key = { "t-${it.id}" }) { ticket ->
                            TicketCard(
                                ticket = ticket,
                                onClick = { onTicketClick(ticket.id) },
                                onToggleSupport = { onToggleSupport(ticket.id) },
                                role = role,
                                commentCount = commentCountOf(ticket.id),
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                    if (canLoadMore) {
                        item(key = "more") {
                            Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(Modifier.size(24.dp), color = BrandIndigo, strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        }
    }
},
sidebarContent = {
    FeedSidebar(
        totalTickets = tickets.size,
        baruCount = tickets.count { it.status == TicketStatus.BARU },
        diprosesCount = tickets.count { it.status == TicketStatus.DIPROSES },
        selesaiCount = tickets.count { it.status == TicketStatus.SELESAI }
    )
},
modifier = modifier
)

    if (showFilterBottomSheet) {
        FilterBottomSheet(
            currentStatus = filter.status,
            currentCategoryId = filter.categoryId,
            currentSort = effectiveSort,
            onApply = { newStatus, newCatId, newSort ->
                onStatusSelected(newStatus)
                onCategorySelected(newCatId)
                onSortSelected(newSort)
            },
            onReset = onResetFilters,
            onDismiss = { showFilterBottomSheet = false }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterBottomSheet(
    currentStatus: String?,
    currentCategoryId: Long?,
    currentSort: FeedSort,
    onApply: (status: String?, categoryId: Long?, sort: FeedSort) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var draftStatus by remember { mutableStateOf(currentStatus) }
    var draftCategoryId by remember { mutableStateOf(currentCategoryId) }
    var draftSort by remember { mutableStateOf(currentSort) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Filter Aduan",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Ink
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (draftStatus != null || draftCategoryId != null || draftSort != FeedSort.LATEST) {
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = DangerRed,
                            modifier = Modifier
                                .clickable {
                                    draftStatus = null
                                    draftCategoryId = null
                                    draftSort = FeedSort.LATEST
                                    onReset()
                                    onDismiss()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Clear, contentDescription = "Tutup", tint = InkMuted, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Section 1: Status Aduan
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = InkMuted
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChipClean(
                            text = "Semua",
                            selected = draftStatus == null,
                            onClick = { draftStatus = null }
                        )
                    }
                    items(STATUS_FILTERS.filter { it.first != null }) { (status, label) ->
                        FilterChipClean(
                            text = label,
                            selected = draftStatus == status,
                            onClick = { draftStatus = status }
                        )
                    }
                }
            }

            // Section 2: Kategori Fasilitas
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Kategori",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = InkMuted
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChipClean(
                            text = "Semua",
                            selected = draftCategoryId == null,
                            onClick = { draftCategoryId = null }
                        )
                    }
                    items(CATEGORY_FILTERS) { (id, label) ->
                        FilterChipClean(
                            text = label,
                            selected = draftCategoryId == id,
                            onClick = { draftCategoryId = id }
                        )
                    }
                }
            }

            // Section 3: Urutan
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Urutkan",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = InkMuted
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipClean(
                        text = "Terbaru",
                        selected = draftSort == FeedSort.LATEST,
                        onClick = { draftSort = FeedSort.LATEST },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChipClean(
                        text = "Paling Banyak Didukung",
                        selected = draftSort == FeedSort.MOST_LIKED,
                        onClick = { draftSort = FeedSort.MOST_LIKED },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Reset and Terapkan Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    onClick = {
                        draftStatus = null
                        draftCategoryId = null
                        draftSort = FeedSort.LATEST
                        onApply(null, null, FeedSort.LATEST)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = FieldBg,
                    border = BorderStroke(1.dp, Hairline),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = InkSoft
                        )
                    }
                }

                Surface(
                    onClick = {
                        onApply(draftStatus, draftCategoryId, draftSort)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = BrandIndigo,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Terapkan",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipClean(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (selected) BrandIndigoSoft else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (selected) BrandIndigo else Color(0xFFE2E8F0)),
        modifier = modifier.height(34.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) BrandIndigo else Color(0xFF475569),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Inbox,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    GlassCard(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = BrandIndigo, modifier = Modifier.size(40.dp))
            Spacer(Modifier.size(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(message, style = MaterialTheme.typography.bodySmall, color = InkSoft, textAlign = TextAlign.Center)
            if (actionLabel != null) {
                Spacer(Modifier.size(12.dp))
                GradientButton(text = actionLabel, onClick = onAction, height = 44.dp, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/** Infinite scroll (feature 2.5): request the next page within 3 items of the end. */
@Composable
private fun LoadMoreEffect(listState: LazyListState, enabled: Boolean, onLoadMore: () -> Unit) {
    val nearEnd by remember(listState, enabled) {
        derivedStateOf {
            val info = listState.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            enabled && info.totalItemsCount > 0 && last >= info.totalItemsCount - 3
        }
    }
    LaunchedEffect(nearEnd) { if (nearEnd) onLoadMore() }
}