package com.example.tiketbantu.ui.screens.monitoring

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.example.tiketbantu.base.BaseViewModel
import com.example.tiketbantu.data.local.dao.CategoryDao
import com.example.tiketbantu.data.local.dao.UserDao
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.ui.theme.CatFasilitas
import com.example.tiketbantu.ui.theme.CatHardware
import com.example.tiketbantu.ui.theme.CatJaringan
import com.example.tiketbantu.ui.theme.CatSoftware
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class CategoryStat(
    val id: Long,
    val name: String,
    val count: Int,
    val percent: Int,
    val weight: Float,
    val color: Color
)

data class SearchFilterState(
    val query: String = "",
    val isSearchActive: Boolean = false,
    val selectedStatus: String? = null,
    val period: String = "Semua"
)

data class MonitoringUiState(
    val total: Int = 0,
    val baruCount: Int = 0,
    val inProcessCount: Int = 0,
    val completedCount: Int = 0,
    val rejectedCount: Int = 0,
    val totalAffected: Int = 0,
    val activeAgentCount: Int = 0,
    val completedPct: Int = 0,
    val categories: List<CategoryStat> = emptyList(),
    val priorityTickets: List<Ticket> = emptyList(),
    val displayedTickets: List<Ticket> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedStatus: String? = null,
    val selectedPeriod: String = "Semua"
)

/**
 * ViewModel for [MonitoringScreen] conforming to Task 4.7 & MVVM architecture.
 *
 * Responsibilities:
 * - Aggregates real-time ticket statistics (Total, Baru/Menunggu, Diproses, Selesai, Ditolak).
 * - Dynamically computes campus solidarity impact (Total Affected supporters) & category breakdown.
 * - Monitors active technician/agent count from Room [UserDao].
 * - Handles search and status-based filtering for Admin monitoring.
 * - Read-only architecture: Admin does not directly transition ticket lifecycle.
 */
class MonitoringViewModel(
    private val ticketRepository: TicketRepository,
    private val categoryDao: CategoryDao,
    private val userDao: UserDao
) : BaseViewModel() {

    private val _filter = MutableStateFlow(SearchFilterState())

    private val ticketsFlow = ticketRepository.getAllTickets(
        query = "",
        categoryId = null,
        status = null,
        sortByMostLiked = true,
        currentUserId = 0L,
        limit = 200
    )

    private val agentsFlow = userDao.getUsersByRole("AGEN")

    val uiState: StateFlow<MonitoringUiState> = combine(
        ticketsFlow,
        agentsFlow,
        _filter
    ) { tickets, agents, filter ->
        val now = System.currentTimeMillis()
        val periodTickets = when (filter.period) {
            "Hari Ini" -> {
                val oneDayAgo = now - 24 * 3600 * 1000L
                tickets.filter { it.createdAt >= oneDayAgo }
            }
            "Minggu Ini" -> {
                val oneWeekAgo = now - 7L * 24 * 3600 * 1000L
                tickets.filter { it.createdAt >= oneWeekAgo }
            }
            "Bulan Ini" -> {
                val oneMonthAgo = now - 30L * 24 * 3600 * 1000L
                tickets.filter { it.createdAt >= oneMonthAgo }
            }
            else -> tickets
        }

        val total = periodTickets.size
        val baru = periodTickets.count { it.status == TicketStatus.BARU }
        val inProcess = periodTickets.count { it.status == TicketStatus.DIPROSES }
        val completed = periodTickets.count { it.status == TicketStatus.SELESAI }
        val rejected = periodTickets.count { it.status == TicketStatus.DITUTUP }
        val affected = periodTickets.sumOf { it.supportCount }
        val activeAgents = agents.count { it.isActive }
        val completedPct = if (total > 0) (completed * 100 / total) else 0

        val jaringanCount = periodTickets.count { it.categoryId == 1L || it.categoryName.contains("Jaringan", ignoreCase = true) }
        val hardwareCount = periodTickets.count { it.categoryId == 2L || it.categoryName.contains("Hardware", ignoreCase = true) }
        val softwareCount = periodTickets.count { it.categoryId == 3L || it.categoryName.contains("Software", ignoreCase = true) }
        val fasilitasCount = periodTickets.count { it.categoryId == 4L || it.categoryName.contains("Fasilitas", ignoreCase = true) }

        val denom = total.coerceAtLeast(1)
        val jaringanPct = if (periodTickets.isEmpty()) 0 else (jaringanCount * 100) / denom
        val hardwarePct = if (periodTickets.isEmpty()) 0 else (hardwareCount * 100) / denom
        val softwarePct = if (periodTickets.isEmpty()) 0 else (softwareCount * 100) / denom
        val fasilitasPct = if (periodTickets.isEmpty()) 0 else (fasilitasCount * 100) / denom

        val wJaringan = if (periodTickets.isEmpty()) 0.25f else (jaringanCount.toFloat() / denom).coerceAtLeast(0.05f)
        val wHardware = if (periodTickets.isEmpty()) 0.25f else (hardwareCount.toFloat() / denom).coerceAtLeast(0.05f)
        val wSoftware = if (periodTickets.isEmpty()) 0.25f else (softwareCount.toFloat() / denom).coerceAtLeast(0.05f)
        val wFasilitas = if (periodTickets.isEmpty()) 0.25f else (fasilitasCount.toFloat() / denom).coerceAtLeast(0.05f)

        val catList = listOf(
            CategoryStat(1L, "Jaringan (WiFi & Internet)", jaringanCount, jaringanPct, wJaringan, CatJaringan),
            CategoryStat(2L, "Hardware (PC, Lab & Proyektor)", hardwareCount, hardwarePct, wHardware, CatHardware),
            CategoryStat(3L, "Software (SIAKAD & LMS)", softwareCount, softwarePct, wSoftware, CatSoftware),
            CategoryStat(4L, "Fasilitas (AC, Kursi & Gedung)", fasilitasCount, fasilitasPct, wFasilitas, CatFasilitas)
        )

        val query = filter.query
        val statusFilter = filter.selectedStatus

        val filtered = periodTickets.filter { ticket ->
            val matchQuery = query.isBlank() ||
                ticket.title.contains(query, ignoreCase = true) ||
                ticket.description.contains(query, ignoreCase = true) ||
                ticket.locationBuilding.contains(query, ignoreCase = true) ||
                ticket.locationRoom.contains(query, ignoreCase = true)
            val matchStatus = statusFilter == null || ticket.status.equals(statusFilter, ignoreCase = true)
            matchQuery && matchStatus
        }

        val priority = periodTickets.sortedByDescending { it.supportCount }.take(3)
        val hasCustomFilter = filter.isSearchActive || statusFilter != null || filter.period != "Semua"

        MonitoringUiState(
            total = total,
            baruCount = baru,
            inProcessCount = inProcess,
            completedCount = completed,
            rejectedCount = rejected,
            totalAffected = affected,
            activeAgentCount = activeAgents,
            completedPct = completedPct,
            categories = catList,
            priorityTickets = priority,
            displayedTickets = if (hasCustomFilter) filtered else priority,
            searchQuery = filter.query,
            isSearchActive = filter.isSearchActive,
            selectedStatus = filter.selectedStatus,
            selectedPeriod = filter.period
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = MonitoringUiState()
    )

    fun onSearchQueryChange(newQuery: String) {
        _filter.update { it.copy(query = newQuery) }
    }

    fun toggleSearch() {
        _filter.update { current ->
            val next = !current.isSearchActive
            if (next) {
                current.copy(isSearchActive = true)
            } else {
                current.copy(isSearchActive = false, query = "", selectedStatus = null)
            }
        }
    }

    fun onStatusFilterChange(status: String?) {
        _filter.update { current ->
            current.copy(
                selectedStatus = if (current.selectedStatus == status) null else status
            )
        }
    }

    fun onPeriodChange(period: String) {
        _filter.update { it.copy(period = period) }
    }

    fun refresh() {
        // Trigger manual refresh / state update if needed
    }
}
