package com.example.tiketbantu.ui.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Roles as stored in the users table (see User.role). */
enum class AppRole(val label: String) {
    PELAPOR("Pelapor"),
    AGEN("Teknisi Sarpras"),
    ADMIN("Admin")
}

/**
 * UI-only session used while the DataStore-backed AuthRepository (task 4.1) is not wired yet.
 * Login / Register / Profile read and write this so every role-specific screen can be demoed.
 * Replace with AuthRepository.getCurrentUser() once the real session exists.
 */
object DemoSession {
    var isLoggedIn by mutableStateOf(false)
    var userId by mutableStateOf(0L)
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var nimNip by mutableStateOf("")
    var role by mutableStateOf(AppRole.PELAPOR)
    var myTicketsInitialStatus by mutableStateOf<String?>(null)

    val firstName: String get() = name.substringBefore(' ')

    fun logout() {
        isLoggedIn = false
        role = AppRole.PELAPOR
        userId = 0L
        name = ""
        email = ""
        nimNip = ""
    }
}
