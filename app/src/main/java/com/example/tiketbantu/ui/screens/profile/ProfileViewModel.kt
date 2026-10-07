package com.example.tiketbantu.ui.screens.profile

import androidx.lifecycle.viewModelScope
import com.example.tiketbantu.base.BaseViewModel
import com.example.tiketbantu.data.local.dao.TicketDao
import com.example.tiketbantu.domain.model.User
import com.example.tiketbantu.domain.repository.AuthRepository
import com.example.tiketbantu.ui.session.DemoSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUserStats(
    val sentCount: Int = 0,
    val supportCount: Int = 0,
    val doneCount: Int = 0
)

/**
 * ViewModel for [ProfileScreen] adhering to Task 4.5 & MVVM architecture.
 *
 * Responsibilities:
 * - Expose reactive current user identity via [AuthRepository.getCurrentUser].
 * - Compute dynamic activity stats in REAL-TIME:
 *   - Agen: Total Tugas (tiket ditugaskan / tersedia) & Telah Tuntas.
 *   - Pelapor: Aduan Dikirim, Didukung, & Telah Tuntas.
 * - Safely clear session via [AuthRepository.logout] upon user exit.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val ticketDao: TicketDao,
) : BaseViewModel() {

    val currentUser: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    val userStats: StateFlow<ProfileUserStats> = authRepository.getCurrentUser()
        .flatMapLatest { user ->
            val userId = user?.id ?: DemoSession.userId
            val role = user?.role?.uppercase() ?: DemoSession.role.name

            if (userId <= 0L) {
                flowOf(ProfileUserStats())
            } else if (role == "AGEN") {
                // Untuk Agen: Tugas dan Telah Tuntas secara Real-Time dari Room
                ticketDao.observeAgentTasks(userId).map { agentTickets ->
                    val totalTasks = agentTickets.size
                    val done = agentTickets.count { it.status.equals("SELESAI", ignoreCase = true) }
                    ProfileUserStats(
                        sentCount = totalTasks,
                        supportCount = 0,
                        doneCount = done
                    )
                }
            } else {
                // Untuk Pelapor: Aduan Dikirim, Didukung, dan Telah Tuntas secara Real-Time dari Room
                combine(
                    ticketDao.observeMine(userId),
                    ticketDao.observeSupported(userId)
                ) { myTickets, supportedTickets ->
                    val sent = myTickets.size
                    val supported = supportedTickets.size
                    val done = myTickets.count { it.status.equals("SELESAI", ignoreCase = true) }
                    ProfileUserStats(
                        sentCount = sent,
                        supportCount = supported,
                        doneCount = done
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ProfileUserStats()
        )

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }
}
