package com.example.tiketbantu.ui.screens.monitoring

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.CircleIconButton
import com.example.tiketbantu.ui.components.FilterPill
import com.example.tiketbantu.ui.components.GlassCard
import com.example.tiketbantu.ui.components.InitialsAvatar
import com.example.tiketbantu.ui.components.StatusPill
import com.example.tiketbantu.ui.components.TagChip
import com.example.tiketbantu.ui.components.locationText
import com.example.tiketbantu.ui.session.DemoSession
import com.example.tiketbantu.ui.theme.BrandCyan
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.BrandIndigoSoft
import com.example.tiketbantu.ui.theme.CatIT
import com.example.tiketbantu.ui.theme.CatRuangan
import com.example.tiketbantu.ui.theme.CatUmum
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

/**
 * Monitoring Sistem screen (Admin Dashboard) matching the reference design.
 * Shows operational sarpras statistics, category distribution, critical tickets, and master data entry points.
 */
@Composable
fun MonitoringScreen(
    onTicketClick: (Long) -> Unit = {},
    onManageUsersClick: () -> Unit = {},
    onManageCategoriesClick: () -> Unit = {},
    viewModel: MonitoringViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val periods = listOf("Hari Ini", "Minggu Ini", "Bulan Ini", "Semester Genap")

    AppBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "header") {
                MonitoringHeader(
                    onRefresh = viewModel::refresh,
                    onSearch = viewModel::toggleSearch,
                    isSearchActive = uiState.isSearchActive
                )
            }

            if (uiState.isSearchActive) {
                item(key = "search_bar") {
                    AdminSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        selectedStatus = uiState.selectedStatus,
                        onStatusSelect = viewModel::onStatusFilterChange
                    )
                }
            }

            item(key = "sync_status") {
                SyncStatusBar(totalEntities = uiState.total)
            }

            item(key = "period_selector") {
                PeriodSection(
                    selectedPeriod = uiState.selectedPeriod,
                    periods = periods,
                    onSelect = viewModel::onPeriodChange
                )
            }

            item(key = "stats_grid") {
                StatsGrid(
                    total = uiState.total,
                    baruCount = uiState.baruCount,
                    inProcess = uiState.inProcessCount,
                    activeAgents = uiState.activeAgentCount,
                    completed = uiState.completedCount,
                    completedPct = uiState.completedPct,
                    affected = uiState.totalAffected
                )
            }

            item(key = "category_distribution") {
                CategoryDistributionCard(categories = uiState.categories)
            }

            item(key = "priority_header") {
                val isFiltering = uiState.isSearchActive || uiState.selectedStatus != null
                val headerTitle = if (isFiltering) "Hasil Penelusuran Aduan" else "Prioritas Dukungan Terbanyak"
                val badgeLabel = if (isFiltering) "${uiState.displayedTickets.size} Tiket" else "${uiState.baruCount.coerceAtLeast(1)} Kritis"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isFiltering) Icons.Outlined.Search else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isFiltering) BrandIndigo else Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Prioritas Dukungan Terbanyak",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Ink,
                        modifier = Modifier.weight(1f)
                    )
                    TagChip(
                        text = badgeLabel,
                        container = if (isFiltering) BrandIndigoSoft else Color(0xFFE0F2FE),
                        content = if (isFiltering) BrandIndigo else Color(0xFF0284C7)
                    )
                }
            }

            item(key = "priority_list") {
                PriorityTicketList(
                    tickets = uiState.displayedTickets,
                    isFiltering = uiState.isSearchActive || uiState.selectedStatus != null,
                    onTicketClick = onTicketClick
                )
            }

            item(key = "master_data_actions") {
                MasterDataActionCard(
                    activeAgentCount = uiState.activeAgentCount,
                    onManageCategories = onManageCategoriesClick,
                    onManageUsers = onManageUsersClick
                )
            }
        }
    }
}

@Composable
private fun MonitoringHeader(
    onRefresh: () -> Unit,
    onSearch: () -> Unit,
    isSearchActive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            InitialsAvatar(
                name = DemoSession.name.ifBlank { "Admin Sarpras" },
                size = 44.dp,
                soft = false
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Statistik",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    letterSpacing = (-0.4).sp
                ),
                color = Ink
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = BrandIndigo,
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = "ADMIN",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "Ruang Operasional Sarpras & Infrastruktur",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = InkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AdminSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedStatus: String?,
    onStatusSelect: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = FieldBg,
            border = BorderStroke(1.dp, Hairline),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = InkMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Ink),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                text = "Cari judul, lokasi, atau deskripsi...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkMuted
                            )
                        }
                        innerTextField()
                    }
                )
                if (query.isNotEmpty()) {
                    CircleIconButton(
                        icon = Icons.Default.Close,
                        contentDescription = "Hapus",
                        onClick = { onQueryChange("") },
                        size = 28.dp,
                        bordered = false,
                        container = Color.Transparent,
                        tint = InkMuted
                    )
                }
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterPill(
                text = "Semua Status",
                selected = selectedStatus == null,
                onClick = { onStatusSelect(null) }
            )
            FilterPill(
                text = "Menunggu",
                selected = selectedStatus == TicketStatus.BARU,
                onClick = { onStatusSelect(TicketStatus.BARU) }
            )
            FilterPill(
                text = "Diproses",
                selected = selectedStatus == TicketStatus.DIPROSES,
                onClick = { onStatusSelect(TicketStatus.DIPROSES) }
            )
            FilterPill(
                text = "Selesai",
                selected = selectedStatus == TicketStatus.SELESAI,
                onClick = { onStatusSelect(TicketStatus.SELESAI) }
            )
            FilterPill(
                text = "Ditolak / Ditutup",
                selected = selectedStatus == TicketStatus.DITUTUP,
                onClick = { onStatusSelect(TicketStatus.DITUTUP) }
            )
        }
    }
}

@Composable
private fun SyncStatusBar(totalEntities: Int) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFFE0F7FA).copy(alpha = 0.6f),
        border = BorderStroke(1.dp, Color(0xFFB2EBF2)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Sync,
                contentDescription = null,
                tint = Color(0xFF00838F),
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Tersinkron: Baru saja • $totalEntities entitas lokal aman",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                color = Color(0xFF006064)
            )
        }
    }
}

@Composable
private fun PeriodSection(
    selectedPeriod: String,
    periods: List<String>,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PERIODE LAPORAN",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = InkMuted
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            periods.forEach { period ->
                FilterPill(
                    text = period,
                    selected = period == selectedPeriod,
                    onClick = { onSelect(period) }
                )
            }
        }
    }
}

@Composable
private fun StatsGrid(
    total: Int,
    baruCount: Int,
    inProcess: Int,
    activeAgents: Int,
    completed: Int,
    completedPct: Int,
    affected: Int
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ringkasan Statistik",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = Ink,
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Update Real-time",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF22C55E)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = Icons.Outlined.ConfirmationNumber,
                iconBg = Color(0xFFE0F7FA),
                iconTint = Color(0xFF00838F),
                badgeText = if (baruCount > 0) "+$baruCount Baru" else "Stabil",
                badgeBg = if (baruCount > 0) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                badgeFg = if (baruCount > 0) Color(0xFF15803D) else InkMuted,
                value = "$total",
                label = "Total Aduan Masuk",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Outlined.Engineering,
                iconBg = Color(0xFFEDE9FE),
                iconTint = BrandIndigo,
                badgeText = "$activeAgents Tim",
                badgeBg = Color(0xFFEDE9FE),
                badgeFg = BrandIndigo,
                value = "$inProcess",
                label = "Sedang Diproses Agen",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = Icons.Outlined.CheckCircle,
                iconBg = Color(0xFFDCFCE7),
                iconTint = Color(0xFF15803D),
                badgeText = "$completedPct%",
                badgeBg = Color(0xFFDCFCE7),
                badgeFg = Color(0xFF15803D),
                value = "$completed",
                label = "Berhasil Selesai ✓",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Outlined.Group,
                iconBg = Color(0xFFFFEDD5),
                iconTint = Color(0xFFC2410C),
                badgeText = "SOLIDARITAS",
                badgeBg = Color(0xFFFFEDD5),
                badgeFg = Color(0xFFC2410C),
                value = "%,d".format(affected).replace(',', '.'),
                label = "Warga Terdampak",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    badgeText: String,
    badgeBg: Color,
    badgeFg: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = badgeBg
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                    color = badgeFg,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
            color = Ink
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = InkSoft,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CategoryDistributionCard(categories: List<CategoryStat> = emptyList()) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Distribusi Kategori Masalah",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Ink
                )
                Text(
                    text = "Beban kerja pemeliharaan sarana & prasarana",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted
                )
            }
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0F7FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.PieChart, contentDescription = null, tint = Color(0xFF00838F), modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.height(16.dp))

        // Segmented progress bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
        ) {
            if (categories.isNotEmpty()) {
                categories.forEach { cat ->
                    Box(Modifier.weight(cat.weight).fillMaxSize().background(cat.color))
                }
            } else {
                Box(Modifier.weight(0.45f).fillMaxSize().background(CatIT))
                Box(Modifier.weight(0.35f).fillMaxSize().background(CatRuangan))
                Box(Modifier.weight(0.20f).fillMaxSize().background(CatUmum))
            }
        }

        Spacer(Modifier.height(16.dp))

        if (categories.isNotEmpty()) {
            categories.forEachIndexed { index, cat ->
                if (index > 0) Spacer(Modifier.height(10.dp))
                CategoryRow(color = cat.color, name = cat.name, percent = "${cat.percent}%", count = "${cat.count} tiket")
            }
        } else {
            CategoryRow(color = CatIT, name = "Teknologi & IT (Lab & WiFi)", percent = "45%", count = "0 tiket")
            Spacer(Modifier.height(10.dp))
            CategoryRow(color = CatRuangan, name = "Fasilitas Ruangan (AC, Kursi, Proyektor)", percent = "35%", count = "0 tiket")
            Spacer(Modifier.height(10.dp))
            CategoryRow(color = CatUmum, name = "Infrastruktur Kampus & Sanitasi", percent = "20%", count = "0 tiket")
        }
    }
}

@Composable
private fun CategoryRow(
    color: Color,
    name: String,
    percent: String,
    count: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = InkSoft,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = percent,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = Ink
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "($count)",
            style = MaterialTheme.typography.bodySmall,
            color = InkMuted
        )
    }
}

@Composable
private fun PriorityTicketList(
    tickets: List<Ticket>,
    isFiltering: Boolean = false,
    onTicketClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (tickets.isEmpty() && isFiltering) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tidak ada aduan yang sesuai dengan filter atau kata kunci pencarian.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp)
                )
            }
        } else {
            val priorityItems = if (tickets.isNotEmpty()) {
                if (isFiltering) tickets else tickets.take(3)
            } else {
                listOf(
                    Ticket(
                        id = 101,
                        title = "Proyektor Lab Multimedia Mati Total Saat Ujian Praktikum",
                        description = "Kendala lampu mati mendadak saat praktikum.",
                        categoryId = 1,
                        categoryName = "Teknologi & IT",
                        locationBuilding = "Gedung Thomas Aquinas",
                        locationFloor = "3",
                        locationRoom = "Ruang 304",
                        status = TicketStatus.BARU,
                        reporterId = 5,
                        reporterName = "BEM FTI",
                        supportCount = 428
                    ),
                    Ticket(
                        id = 102,
                        title = "AC Sentral Gedung Kuliah Bersama Bocor & Berisik",
                        description = "Air menetes ke selasar utama.",
                        categoryId = 2,
                        categoryName = "Fasilitas Ruangan",
                        locationBuilding = "GKB 1",
                        locationFloor = "2",
                        locationRoom = "Selasar Barat",
                        status = TicketStatus.DIPROSES,
                        reporterId = 6,
                        reporterName = "Mahasiswa",
                        agentName = "Pak Bambang (MEP)",
                        supportCount = 312
                    ),
                    Ticket(
                        id = 103,
                        title = "Koneksi Access Point Eduroam Perpustakaan Pusat Putus-Nyambung",
                        description = "Sinyal hilang timbul saat banyak pengunjung.",
                        categoryId = 1,
                        categoryName = "Teknologi & IT",
                        locationBuilding = "Perpustakaan Pusat",
                        locationFloor = "1",
                        locationRoom = "Area Baca",
                        status = TicketStatus.BARU,
                        reporterId = 7,
                        reporterName = "Tim Mahasiswa Skripsi",
                        supportCount = 289
                    )
                )
            }

            priorityItems.forEach { ticket ->
                PriorityTicketCard(ticket = ticket, onClick = { onTicketClick(ticket.id) })
            }
        }
    }
}

@Composable
private fun PriorityTicketCard(
    ticket: Ticket,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                StatusPill(status = ticket.status)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = ticket.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, lineHeight = 20.sp),
                    color = Ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = ticket.locationText(),
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(12.dp))
            // Large circular support count badge
            Surface(
                shape = CircleShape,
                color = SupportOrangeSoft,
                border = BorderStroke(1.dp, SupportOrange.copy(alpha = 0.3f)),
                modifier = Modifier.size(72.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${ticket.supportCount}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = SupportOrangeText
                    )
                    Text(
                        text = "DUKUNGAN",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp),
                        color = SupportOrangeText
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))
        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val author = if (ticket.status == TicketStatus.DIPROSES && ticket.agentName != null) {
                "Teknisi: ${ticket.agentName}"
            } else {
                "Pelapor: ${ticket.reporterName.ifBlank { "Civitas Kampus" }}"
            }
            Text(
                text = author,
                style = MaterialTheme.typography.labelSmall,
                color = InkSoft,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                val actionLabel = if (ticket.status == TicketStatus.BARU) "Disposisikan" else "Cek Progres"
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = BrandIndigo
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = BrandIndigo,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun MasterDataActionCard(
    activeAgentCount: Int = 18,
    onManageCategories: () -> Unit,
    onManageUsers: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Aksi Master Data & Sistem",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = Ink
        )
        Text(
            text = "Konfigurasi operasional dan sinkronisasi data",
            style = MaterialTheme.typography.bodySmall,
            color = InkMuted
        )

        Spacer(Modifier.height(16.dp))

        // Row 1: Kelola Kategori
        Surface(
            onClick = onManageCategories,
            shape = RoundedCornerShape(16.dp),
            color = FieldBg,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F7FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Category,
                        contentDescription = null,
                        tint = Color(0xFF00838F),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Kelola Kategori",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = InkMuted
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Row 2: Kelola Akun Agen & Teknisi
        Surface(
            onClick = onManageUsers,
            shape = RoundedCornerShape(16.dp),
            color = FieldBg,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDE9FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ManageAccounts,
                        contentDescription = null,
                        tint = BrandIndigo,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Kelola Akun Agen & Teknisi",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = InkMuted
                )
            }
        }
    }
}
