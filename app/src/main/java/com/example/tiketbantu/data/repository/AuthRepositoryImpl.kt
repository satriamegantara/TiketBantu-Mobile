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

        val entity = userDao.getUserByEmail(trimmedEmail)
            ?: return Result.failure(IllegalArgumentException("Email tidak terdaftar"))

        if (!entity.isActive) {
            return Result.failure(IllegalStateException("Akun ini telah dinonaktifkan oleh administrator."))
        }

        val stored = entity.passwordHash
        val isHashed = stored.length == 64 && stored.all { it in '0'..'9' || it in 'a'..'f' }
        val isPasswordMatch = if (isHashed) stored == passwordHash else hashSha256(stored) == passwordHash

        if (!isPasswordMatch) {
            return Result.failure(IllegalArgumentException("Kata sandi salah"))
        }

        val domainUser = User(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            nimNip = entity.nimNip,
            role = entity.role,
            isActive = entity.isActive
        )

        sessionManager.saveSession(domainUser)
        syncDemoSession(domainUser)

        return Result.success(domainUser)
    }

    private fun hashSha256(input: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
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
            return Result.failure(IllegalArgumentException("Email sudah terdaftar"))
        }

        val newId = userDao.insertUser(
            UserEntity(
                name = name.trim(),
                email = trimmedEmail,
                nimNip = nimNip?.trim()?.ifBlank { null },
                passwordHash = passwordHash,
                role = "PELAPOR",
                isActive = true
            )
        )

        val domainUser = User(
            id = newId,
            name = name.trim(),
            email = trimmedEmail,
            nimNip = nimNip?.trim()?.ifBlank { null },
            role = "PELAPOR",
            isActive = true
        )

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
