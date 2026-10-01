package com.example.tiketbantu.data.repository

import com.example.tiketbantu.domain.model.User
import com.example.tiketbantu.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * TEMPORARY stand-in for the DataStore-backed AuthRepository (Anggota 4, task 4.1).
 * Always logged in. role = "PELAPOR" (default) -> demo reporter id 1.
 * role = "AGEN" -> "Pak Budi" (id 20), the assigned agent of demo ticket #2 (DIPROSES), so the
 * status-update flow (task 3.6) can be tried. Remove once the real implementation is bound in Koin.
 */
class FakeAuthRepository(role: String = "PELAPOR") : AuthRepository {

    private val currentUser = MutableStateFlow<User?>(
        if (role == "AGEN") {
            User(id = 20L, name = "Pak Budi", email = "budi@kampus.ac.id", role = "AGEN")
        } else {
            User(id = 1L, name = "Pengguna Demo", email = "demo@kampus.ac.id", role = role)
        }
    )

    override fun getCurrentUser(): Flow<User?> = currentUser.asStateFlow()

    override suspend fun login(email: String, passwordHash: String): Result<User> =
        currentUser.value?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException("Fake auth: tidak ada user"))

    override suspend fun register(
        name: String,
        email: String,
        nimNip: String?,
        passwordHash: String
    ): Result<User> = Result.failure(UnsupportedOperationException("Fake auth: register belum tersedia"))

    override suspend fun logout() {
        currentUser.value = null
    }

    override suspend fun isLoggedIn(): Boolean = currentUser.value != null
}
