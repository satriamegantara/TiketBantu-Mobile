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

        // Normalisasi input email agar user dapat login menggunakan domain @kampus.ac.id, @tiketbantu.com, maupun shorthand username
        val normalizedEmail = when (trimmedEmail) {
            "admin", "admin@tiketbantu.com" -> "admin@kampus.ac.id"
            "agen", "agen.jaringan", "agen@kampus.ac.id", "agen.jaringan@kampus.ac.id", "agen.jaringan@tiketbantu.com", "agen@tiketbantu.com", "joko.santoso@kampus.ac.id" -> "agen.jaringan@tiketbantu.com"
            "agen.hardware", "agen.hardware@tiketbantu.com", "hardware@kampus.ac.id", "agen.hardware@kampus.ac.id" -> "agen.hardware@tiketbantu.com"
            "agen.software", "agen.software@tiketbantu.com", "software@kampus.ac.id", "agen.software@kampus.ac.id" -> "agen.software@tiketbantu.com"
            "agen.fasilitas", "agen.fasilitas@tiketbantu.com", "fasilitas@kampus.ac.id", "agen.fasilitas@kampus.ac.id" -> "agen.fasilitas@tiketbantu.com"
            "satcarzensyaf", "satriapancarzenasyafa", "satriapancarzenasyafa@kampus.ac.id", "emily.johnson@kampus.ac.id", "mahasiswa@kampus.ac.id", "user@kampus.ac.id" -> "satcarzensyaf@kampus.ac.id"
            "ahmad", "ahmad.fauzi", "ahmad.fauzi@kampus.ac.id", "ahmad.dosen@kampus.ac.id", "dosen@kampus.ac.id", "dosen" -> "ahmad.fauzi@kampus.ac.id"
            "rina", "rina.kartika", "rina.kartika@kampus.ac.id" -> "rina.kartika@kampus.ac.id"
            "dimas", "dimas.putra", "dimas.putra@kampus.ac.id" -> "dimas.putra@kampus.ac.id"
            "nadia", "nadia.safitri", "nadia.safitri@kampus.ac.id" -> "nadia.safitri@kampus.ac.id"
            else -> if (!trimmedEmail.contains("@")) "$trimmedEmail@kampus.ac.id" else trimmedEmail
        }

        val entity = userDao.getUserByEmail(normalizedEmail)
            ?: userDao.getUserByEmail(trimmedEmail)
            ?: (when (trimmedEmail) {
                "agen.jaringan@tiketbantu.com", "agen@tiketbantu.com" -> userDao.getUserByEmail("agen@kampus.ac.id")
                "agen@kampus.ac.id", "agen.jaringan@kampus.ac.id" -> userDao.getUserByEmail("agen.jaringan@tiketbantu.com")
                "agen.hardware@tiketbantu.com" -> userDao.getUserByEmail("agen.hardware@kampus.ac.id")
                "agen.hardware@kampus.ac.id" -> userDao.getUserByEmail("agen.hardware@tiketbantu.com")
                "agen.software@tiketbantu.com" -> userDao.getUserByEmail("agen.software@kampus.ac.id")
                "agen.software@kampus.ac.id" -> userDao.getUserByEmail("agen.software@tiketbantu.com")
                "agen.fasilitas@tiketbantu.com" -> userDao.getUserByEmail("agen.fasilitas@kampus.ac.id")
                "agen.fasilitas@kampus.ac.id" -> userDao.getUserByEmail("agen.fasilitas@tiketbantu.com")
                else -> null
            })
            ?: return Result.failure(IllegalArgumentException("Email tidak terdaftar"))

        if (!entity.isActive) {
            return Result.failure(IllegalStateException("Akun ini telah dinonaktifkan oleh administrator."))
        }

        val stored = entity.passwordHash
        val isHashed = stored.length == 64 && stored.all { it in '0'..'9' || it in 'a'..'f' }
        val isPasswordMatch = if (isHashed) {
            stored == passwordHash
        } else {
            hashSha256(stored) == passwordHash || stored == passwordHash
        }

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
