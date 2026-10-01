package com.example.tiketbantu.ui.screens.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.example.tiketbantu.base.UiState
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.model.TicketStatus
import org.koin.androidx.compose.koinViewModel

/**
 * Public complaint feed (Dashboard content). Stateful wrapper: wires [FeedViewModel] to the
 * stateless [FeedContent]. Hook it into the NavGraph, e.g.
 *
 *   composable<Screen.Dashboard> {
 *       FeedScreen(onTicketClick = { id -> navController.navigate(Screen.TicketDetail(id)) })
 *   }
 */
@Composable
fun FeedScreen(
    onTicketClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel()
) {
    val feedState by viewModel.feedState.collectAsState()
    val searchInput by viewModel.searchInput.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val effectiveSort by viewModel.effectiveSort.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val canLoadMore by viewModel.canLoadMore.collectAsState()

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

    Box(modifier = modifier.fillMaxSize()) {
        FeedContent(
            feedState = feedState,
            searchInput = searchInput,
            filter = filter,
            effectiveSort = effectiveSort,
            isRefreshing = isRefreshing,
            canLoadMore = canLoadMore,
            onSearchChange = viewModel::onSearchChange,
            onStatusSelected = viewModel::onStatusSelected,
            onSortSelected = viewModel::onSortSelected,
            onRefresh = viewModel::refresh,
            onLoadMore = viewModel::loadMore,
            onRetry = viewModel::retry,
            onTicketClick = onTicketClick,
            onToggleSupport = viewModel::onToggleSupport
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
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
    onSearchChange: (String) -> Unit,
    onStatusSelected: (String?) -> Unit,
    onSortSelected: (FeedSort) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onTicketClick: (Long) -> Unit,
    onToggleSupport: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        SearchField(
            value = searchInput,
            onValueChange = onSearchChange,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp)
        )
        FilterRow(
            filter = filter,
            effectiveSort = effectiveSort,
            onSortSelected = onSortSelected,
            onStatusSelected = onStatusSelected,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (feedState) {
                UiState.Idle, UiState.Loading -> CenteredBox { CircularProgressIndicator() }

                is UiState.Error -> CenteredBox {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = feedState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = onRetry) { Text("Coba Lagi") }
                    }
                }

                is UiState.Success -> {
                    if (feedState.data.isEmpty()) {
                        CenteredBox {
                            Text(
                                text = if (searchInput.isNotBlank() || filter.status != null) {
                                    "Tidak ada aduan yang cocok dengan pencarian atau filter."
                                } else {
                                    "Belum ada aduan."
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    } else {
                        TicketList(
                            tickets = feedState.data,
                            canLoadMore = canLoadMore,
                            onLoadMore = onLoadMore,
                            onTicketClick = onTicketClick,
                            onToggleSupport = onToggleSupport
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Cari judul atau deskripsi aduan") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Hapus pencarian")
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
    )
}

private val STATUS_FILTERS: List<Pair<String?, String>> = listOf(
    null to "Semua",
    TicketStatus.BARU to "Baru",
    TicketStatus.DIPROSES to "Diproses",
    TicketStatus.SELESAI to "Selesai",
    TicketStatus.DITUTUP to "Ditutup"
)

private fun FeedSort.label(): String = when (this) {
    FeedSort.LATEST -> "Terbaru"
    FeedSort.MOST_LIKED -> "Terpopuler"
}

@Composable
private fun FilterRow(
    filter: FeedFilter,
    effectiveSort: FeedSort,
    onSortSelected: (FeedSort) -> Unit,
    onStatusSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(FeedSort.entries.toList(), key = { "sort-${it.name}" }) { sort ->
            FilterChip(
                selected = effectiveSort == sort,
                onClick = { onSortSelected(sort) },
                label = { Text(sort.label()) }
            )
        }
        item(key = "filter-divider") {
            VerticalDivider(modifier = Modifier.height(24.dp))
        }
        items(STATUS_FILTERS, key = { "status-${it.first ?: "ALL"}" }) { (status, label) ->
            FilterChip(
                selected = filter.status == status,
                onClick = { onStatusSelected(status) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun TicketList(
    tickets: List<Ticket>,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit,
    onTicketClick: (Long) -> Unit,
    onToggleSupport: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // The repository already returns active tickets first; partition only locates the divider.
    val (active, finished) = remember(tickets) {
        tickets.partition { !TicketStatus.isFinished(it.status) }
    }

    LoadMoreEffect(listState = listState, enabled = canLoadMore, onLoadMore = onLoadMore)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(active, key = { "ticket-${it.id}" }) { ticket ->
            TicketCard(
                ticket = ticket,
                onClick = { onTicketClick(ticket.id) },
                onToggleSupport = { onToggleSupport(ticket.id) }
            )
        }
        if (finished.isNotEmpty()) {
            item(key = "finished-header") {
                Text(
                    text = "Aduan selesai dan ditutup",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            items(finished, key = { "ticket-${it.id}" }) { ticket ->
                TicketCard(
                    ticket = ticket,
                    onClick = { onTicketClick(ticket.id) },
                    onToggleSupport = { onToggleSupport(ticket.id) }
                )
            }
        }
        if (canLoadMore) {
            item(key = "load-more-footer") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

/** Calls [onLoadMore] once the user scrolls within 3 items of the end of the list. */
@Composable
private fun LoadMoreEffect(
    listState: LazyListState,
    enabled: Boolean,
    onLoadMore: () -> Unit
) {
    val nearEnd by remember(listState, enabled) {
        derivedStateOf {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            enabled && info.totalItemsCount > 0 && lastVisible >= info.totalItemsCount - 3
        }
    }
    LaunchedEffect(nearEnd) {
        if (nearEnd) onLoadMore()
    }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        content()
    }
}
