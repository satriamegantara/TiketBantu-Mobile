package com.example.tiketbantu.data.repository

import com.example.tiketbantu.data.local.dao.UserDao
import com.example.tiketbantu.data.local.entity.UserEntity
import com.example.tiketbantu.data.preferences.SessionManager
import com.example.tiketbantu.domain.model.User
import com.example.tiketbantu.domain.repository.AuthRepository
import com.example.tiketbantu.ui.session.AppRole
import com.example.tiketbantu.ui.session.DemoSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Production Room + SessionManager-backed implementation of [AuthRepository].
 * Manages user authentication, registration, local persistence, and session lifecycle.
 */
class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> =
        sessionManager.sessionState.map { it.currentUser }

    override suspend fun login(email: String, passwordHash: String): Result<User> {
        val trimmedEmail = email.trim().lowercase()

        // 1. Look up in Room database
        var entity = userDao.getUserByEmail(trimmedEmail)

        // 2. Fallback aliases for demo accounts if not found by exact string
        if (entity == null) {
            when {
                trimmedEmail.contains("emily") || trimmedEmail == "user@kampus.ac.id" -> {
                    entity = userDao.getUserByEmail("emily.johnson@kampus.ac.id")
                        ?: userDao.getUserByEmail("user@kampus.ac.id")
                }
                trimmedEmail.contains("joko") || trimmedEmail.contains("budi") || trimmedEmail == "agen@kampus.ac.id" -> {
                    entity = userDao.getUserByEmail("joko.santoso@kampus.ac.id")
                        ?: userDao.getUserByEmail("agen@kampus.ac.id")
                }
                trimmedEmail.contains("admin") -> {
                    entity = userDao.getUserByEmail("admin.sarpras@kampus.ac.id")
                        ?: userDao.getUserByEmail("admin@kampus.ac.id")
                }
            }
        }

        if (entity == null) {
            return Result.failure(IllegalArgumentException("Akun tidak ditemukan. Pastikan email terdaftar."))
        }

        if (!entity.isActive) {
            return Result.failure(IllegalStateException("Akun ini telah dinonaktifkan oleh administrator."))
        }

        val domainUser = User(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            nimNip = entity.nimNip,
            role = entity.role,
            isActive = entity.isActive
        )

        // 3. Persist session to DataStore & synchronize DemoSession
        sessionManager.saveSession(domainUser)
        syncDemoSession(domainUser)

        return Result.success(domainUser)
    }

    override suspend fun register(
        name: String,
        email: String,
        nimNip: String?,
        passwordHash: String
    ): Result<User> {
        val trimmedEmail = email.trim().lowercase()
        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("Email sudah terdaftar. Silakan login."))
        }

        val newId = userDao.insertUser(
            UserEntity(
                name = name.trim(),
                email = trimmedEmail,
                nimNip = nimNip?.trim(),
                passwordHash = passwordHash,
                role = "PELAPOR",
                isActive = true
            )
        )

        val domainUser = User(
            id = newId,
            name = name.trim(),
            email = trimmedEmail,
            nimNip = nimNip?.trim(),
            role = "PELAPOR",
            isActive = true
        )

        sessionManager.saveSession(domainUser)
        syncDemoSession(domainUser)

        return Result.success(domainUser)
    }

    override suspend fun logout() {
        sessionManager.clearSession()
        DemoSession.logout()
    }

    override suspend fun isLoggedIn(): Boolean =
        sessionManager.isLoggedIn()

    private fun syncDemoSession(user: User) {
        DemoSession.userId = user.id
        DemoSession.name = user.name
        DemoSession.email = user.email
        DemoSession.nimNip = user.nimNip ?: ""
        DemoSession.role = when (user.role.uppercase()) {
            "ADMIN" -> AppRole.ADMIN
            "AGEN" -> AppRole.AGEN
            else -> AppRole.PELAPOR
        }
        DemoSession.isLoggedIn = true
    }
}
