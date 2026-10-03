package com.example.tiketbantu.ui.screens.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tiketbantu.base.UiState
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.domain.repository.CommentRepository
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.AppInput
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
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import com.example.tiketbantu.ui.theme.SuccessText
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
 * Wired to [FeedViewModel]; claim (3.1.2) goes straight to the repository until a use case exists.
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
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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

    AppBackground(modifier) {
        FeedContent(
            feedState = feedState,
            searchInput = searchInput,
            filter = filter,
            effectiveSort = effectiveSort,
            isRefreshing = isRefreshing,
            canLoadMore = canLoadMore,
            role = DemoSession.role,
            userName = DemoSession.name,
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
    var showStatusFilter by rememberSaveable { mutableStateOf(false) }

    val listState = rememberLazyListState()
    LoadMoreEffect(listState, canLoadMore, onLoadMore)

    PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = onRefresh, modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(key = "header") {
                UserGreetingHeader(userName = userName, onAvatarClick = onProfileClick)
            }

            item(key = "search") {
                SearchBar(
                    value = searchInput,
                    onValueChange = onSearchChange,
                    filterActive = showStatusFilter || filter.status != null,
                    onFilterClick = { showStatusFilter = !showStatusFilter },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item(key = "filters") {
                Column {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterPill(
                                text = "Semua",
                                selected = filter.categoryId == null && effectiveSort == FeedSort.LATEST,
                                onClick = { onCategorySelected(null); onSortSelected(FeedSort.LATEST) }
                            )
                        }
                        item {
                            FilterPill(
                                text = "Terbanyak Dukungan",
                                icon = Icons.Default.LocalFireDepartment,
                                selected = effectiveSort == FeedSort.MOST_LIKED,
                                onClick = {
                                    onSortSelected(if (effectiveSort == FeedSort.MOST_LIKED) FeedSort.LATEST else FeedSort.MOST_LIKED)
                                }
                            )
                        }
                        items(CATEGORY_FILTERS) { (id, label) ->
                            FilterPill(
                                text = label,
                                selected = filter.categoryId == id,
                                onClick = { onCategorySelected(if (filter.categoryId == id) null else id) }
                            )
                        }
                    }
                    AnimatedVisibility(showStatusFilter) {
                        LazyRow(
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(STATUS_FILTERS) { (status, label) ->
                                FilterPill(text = label, selected = filter.status == status, onClick = { onStatusSelected(status) })
                            }
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(SuccessText))
                            Spacer(Modifier.width(6.dp))
                            Text("Pembaruan otomatis", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                        }
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
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    filterActive: Boolean,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppInput(
        value = value,
        onValueChange = onValueChange,
        placeholder = "Cari aduan fasilitas, lab, kelas, wifi...",
        modifier = modifier,
        leadingIcon = { Icon(Icons.Default.Search, null, tint = InkMuted) },
        trailingIcon = {
            Row {
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) { Icon(Icons.Default.Clear, "Hapus", tint = InkMuted) }
                }
                IconButton(onClick = onFilterClick) {
                    Icon(Icons.Default.Tune, "Filter status", tint = if (filterActive) BrandIndigo else InkSoft)
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    GlassCard(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.Inbox, null, tint = BrandIndigo, modifier = Modifier.size(40.dp))
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
