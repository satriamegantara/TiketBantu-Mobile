package com.example.tiketbantu.data.repository

import com.example.tiketbantu.data.local.dao.UserDao
import com.example.tiketbantu.data.local.entity.UserEntity
import com.example.tiketbantu.data.preferences.SessionManager
import com.example.tiketbantu.domain.model.User
import com.example.tiketbantu.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Concrete implementation of [AuthRepository] using Room [UserDao] and [SessionManager].
 *
 * Responsibilities:
 * - Authenticate user credentials against the local Room DB.
 * - Register new users with default role PELAPOR, rejecting duplicate emails.
 * - Manage session state persistence via DataStore [SessionManager].
 */
class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> =
        sessionManager.sessionState.map { it.currentUser }

    override suspend fun login(email: String, passwordHash: String): Result<User> {
        val userEntity = userDao.getUserByEmail(email)
            ?: return Result.failure(IllegalArgumentException("Email tidak terdaftar"))

        if (!userEntity.isActive) {
            return Result.failure(IllegalStateException("Akun ini telah dinonaktifkan"))
        }

        val stored = userEntity.passwordHash
        val isHashed = stored.length == 64 && stored.all { it in '0'..'9' || it in 'a'..'f' }
        val isPasswordMatch = if (isHashed) stored == passwordHash else hashSha256(stored) == passwordHash

        if (!isPasswordMatch) {
            return Result.failure(IllegalArgumentException("Kata sandi salah"))
        }

        val user = userEntity.toDomain()
        sessionManager.saveSession(user)
        return Result.success(user)
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
        val existingUser = userDao.getUserByEmail(email)
        if (existingUser != null) {
            return Result.failure(IllegalArgumentException("Email sudah terdaftar"))
        }

        val newUserEntity = UserEntity(
            name = name,
            email = email,
            nimNip = nimNip,
            passwordHash = passwordHash,
            role = "PELAPOR",
            isActive = true
        )

        val newId = userDao.insertUser(newUserEntity)
        val user = newUserEntity.copy(id = newId).toDomain()
        return Result.success(user)
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    override suspend fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    private fun UserEntity.toDomain(): User = User(
        id = id,
        name = name,
        email = email,
        nimNip = nimNip,
        role = role,
        isActive = isActive
    )
}
