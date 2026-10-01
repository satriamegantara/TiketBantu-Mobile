package com.example.tiketbantu.domain.repository

import com.example.tiketbantu.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract for Authentication & Session management.
 */
interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun login(email: String, passwordHash: String): Result<User>
    suspend fun register(name: String, email: String, nimNip: String?, passwordHash: String): Result<User>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}
