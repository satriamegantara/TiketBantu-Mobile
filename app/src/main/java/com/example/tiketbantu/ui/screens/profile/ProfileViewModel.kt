package com.example.tiketbantu.ui.screens.profile

import androidx.lifecycle.viewModelScope
import com.example.tiketbantu.base.BaseViewModel
import com.example.tiketbantu.data.local.dao.SupportDao
import com.example.tiketbantu.data.local.dao.TicketDao
import com.example.tiketbantu.domain.model.User
import com.example.tiketbantu.domain.repository.AuthRepository
import com.example.tiketbantu.ui.session.AppRole
import com.example.tiketbantu.ui.session.DemoSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
 * - Compute dynamic activity stats (Sent tickets, Supported tickets, Completed tickets) from Room DAOs.
 * - Manage role switching for demo accounts with verified credentials.
 * - Safely clear session via [AuthRepository.logout] upon user exit.
 */
class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val ticketDao: TicketDao,
    private val supportDao: SupportDao
) : BaseViewModel() {

    val currentUser: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    private val _userStats = MutableStateFlow(ProfileUserStats())
    val userStats: StateFlow<ProfileUserStats> = _userStats.asStateFlow()

    init {
        viewModelScope.launch {
            currentUser.collectLatest { user ->
                val userId = user?.id ?: DemoSession.userId
                if (userId > 0) {
                    launch {
                        ticketDao.observeMine(userId).collect { myTickets ->
                            val sent = myTickets.size
                            val done = myTickets.count { it.status.equals("SELESAI", ignoreCase = true) }
                            _userStats.value = _userStats.value.copy(sentCount = sent, doneCount = done)
                        }
                    }
                    launch {
                        supportDao.getSupportedTicketIdsByUser(userId).collect { supportedList ->
                            _userStats.value = _userStats.value.copy(supportCount = supportedList.size)
                        }
                    }
                }
            }
        }
    }

    fun switchRoleDemo(targetRole: AppRole) {
        viewModelScope.launch {
            DemoSession.loginAs(targetRole)
            val (email, rawPassword) = when (targetRole) {
                AppRole.PELAPOR -> "emily.johnson@kampus.ac.id" to "user123"
                AppRole.AGEN -> "joko.santoso@kampus.ac.id" to "agen123"
                AppRole.ADMIN -> "admin@kampus.ac.id" to "admin123"
            }
            authRepository.login(email, hashSha256(rawPassword))
        }
    }

    private fun hashSha256(input: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }
}
