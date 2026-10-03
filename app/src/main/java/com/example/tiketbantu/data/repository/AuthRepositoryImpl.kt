package com.example.tiketbantu.data.repository

import com.example.tiketbantu.data.local.dao.UserDao
import com.example.tiketbantu.data.local.entity.UserEntity
import com.example.tiketbantu.data.preferences.SessionManager
import com.example.tiketbantu.domain.model.User
import com.example.tiketbantu.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 */
class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> =
        sessionManager.sessionState.map { it.currentUser }

    override suspend fun login(email: String, passwordHash: String): Result<User> {

        }

        }

    }

    override suspend fun register(
        name: String,
        email: String,
        nimNip: String?,
        passwordHash: String
    ): Result<User> {
        }

            passwordHash = passwordHash,
            role = "PELAPOR",
            isActive = true
        )

    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }


}
