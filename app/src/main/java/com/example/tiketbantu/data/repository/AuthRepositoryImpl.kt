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
 * Concrete implementation of [AuthRepository] using Room [UserDao] and [SessionManager].
 *
 * Responsibilities:
 * - Authenticate user credentials against the local Room DB (supports hashed and plaintext seeders).
 * - Register new users with default role PELAPOR, rejecting duplicate emails.
 * - Manage session state persistence via DataStore [SessionManager] and synchronize [DemoSession].
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

        val domainUser = entity.toDomain()

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

        val newUserEntity = UserEntity(
            name = name.trim(),
            email = trimmedEmail,
            nimNip = nimNip?.trim()?.ifBlank { null },
            passwordHash = passwordHash,
            role = "PELAPOR",
            isActive = true
        )

        val newId = userDao.insertUser(newUserEntity)
        val domainUser = newUserEntity.copy(id = newId).toDomain()

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

    private fun UserEntity.toDomain(): User = User(
        id = id,
        name = name,
        email = email,
        nimNip = nimNip,
        role = role,
        isActive = isActive
    )
}
