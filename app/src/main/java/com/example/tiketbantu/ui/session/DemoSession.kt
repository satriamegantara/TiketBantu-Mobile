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
    var userId by mutableStateOf(3L)
    var name by mutableStateOf("Emily Johnson")
    var email by mutableStateOf("emily.johnson@kampus.ac.id")
    var nimNip by mutableStateOf("2021110045")
    var role by mutableStateOf(AppRole.PELAPOR)

    val firstName: String get() = name.substringBefore(' ')

    fun loginAs(role: AppRole) {
        this.role = role
        when (role) {
            AppRole.PELAPOR -> {
                userId = 3L; name = "Emily Johnson"; email = "emily.johnson@kampus.ac.id"; nimNip = "2021110045"
            }
            AppRole.AGEN -> {
                userId = 2L; name = "Joko Santoso"; email = "joko.santoso@kampus.ac.id"; nimNip = "198703122010"
            }
            AppRole.ADMIN -> {
                userId = 1L; name = "Admin Sarpras"; email = "admin.sarpras@kampus.ac.id"; nimNip = "-"
            }
        }
        isLoggedIn = true
    }

    fun logout() {
        isLoggedIn = false
    }
}
