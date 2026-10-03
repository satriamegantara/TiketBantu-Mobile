package com.example.tiketbantu.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.tiketbantu.base.SessionState
import com.example.tiketbantu.domain.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

// Top-level DataStore delegate — one instance per app process
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tiketbantu_session")

/**
 * **Shared Session State Holder** for TiketBantu Mobile.
 *
 * Acts as the single in-memory + persisted source of truth for the current user session.
 * All ViewModels that need session info should inject and observe [sessionState] rather
 * than reading DataStore directly.
 *
 * ### Responsibilities
 * - Read/write session credentials to [DataStore] (survives process death).
 * - Expose [sessionState] as a hot [StateFlow] so the NavGraph and feature ViewModels
 *   can react to login / logout without polling.
 *
 * ### Integration in NavGraph
 * ```kotlin
 * val session by sessionManager.sessionState.collectAsStateWithLifecycle()
 * LaunchedEffect(session.isLoggedIn) {
 *     if (!session.isLoggedIn) navController.navigate(Screen.Login)
 * }
 * ```
 *
 * @param context      Application context for DataStore access.
 * @param appScope     Application-wide [CoroutineScope] to keep DataStore collector alive.
 */
class SessionManager(
    private val context: Context,
    appScope: CoroutineScope,
) {
    // ── DataStore preference keys ─────────────────────────────────────────────

    private object Keys {
        val IS_LOGGED_IN  = booleanPreferencesKey("is_logged_in")
        val USER_ID       = longPreferencesKey("user_id")
        val USER_NAME     = stringPreferencesKey("user_name")
        val USER_EMAIL    = stringPreferencesKey("user_email")
        val USER_NIM_NIP  = stringPreferencesKey("user_nim_nip")
        val USER_ROLE     = stringPreferencesKey("user_role")
    }

    // ── In-memory StateFlow (hot, shared) ─────────────────────────────────────

    private val _sessionState = MutableStateFlow(SessionState.Initial)

    /**
     * Hot [StateFlow] emitting the latest [SessionState].
     * Subscribe in NavGraph and any ViewModel that requires session info.
     */
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    // ── Cold Flow from DataStore ──────────────────────────────────────────────

    private val sessionFlow: Flow<SessionState> = context.dataStore.data
        .map { prefs ->
            val loggedIn = prefs[Keys.IS_LOGGED_IN] ?: false
            if (loggedIn) {
                val user = User(
                    id       = prefs[Keys.USER_ID]    ?: 0L,
                    name     = prefs[Keys.USER_NAME]  ?: "",
                    email    = prefs[Keys.USER_EMAIL]  ?: "",
                    nimNip   = prefs[Keys.USER_NIM_NIP],
                    role     = prefs[Keys.USER_ROLE]  ?: "PELAPOR",
                )
                SessionState(isLoggedIn = true, currentUser = user, isLoading = false)
            } else {
                SessionState.LoggedOut
            }
        }

    init {
        // Bridge cold DataStore Flow → hot StateFlow, alive for the app lifetime
        sessionFlow
            .onEach { state -> _sessionState.value = state }
            .launchIn(appScope)
    }

    // ── Write operations ──────────────────────────────────────────────────────

    /**
     * Persists the [user] session to DataStore and immediately updates [sessionState].
     * Call this after a successful login operation.
     */
    suspend fun saveSession(user: User) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = true
            prefs[Keys.USER_ID]      = user.id
            prefs[Keys.USER_NAME]    = user.name
            prefs[Keys.USER_EMAIL]   = user.email
            prefs[Keys.USER_NIM_NIP] = user.nimNip ?: ""
            prefs[Keys.USER_ROLE]    = user.role
        }
        // StateFlow is updated reactively via the DataStore collector above
    }

    /**
     * Clears the stored session from DataStore and resets [sessionState] to [SessionState.LoggedOut].
     * Call this on Logout.
     */
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = false
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.USER_NAME)
            prefs.remove(Keys.USER_EMAIL)
            prefs.remove(Keys.USER_NIM_NIP)
            prefs.remove(Keys.USER_ROLE)
        }
    }

    /**
     * Returns the currently active [User], or null if not logged in.
     * Prefer observing [sessionState] for reactive UI over this synchronous accessor.
     */
    fun currentUser(): User? = _sessionState.value.currentUser

    /**
     * Returns true if a valid session is currently active.
     * Prefer observing [sessionState] for reactive UI over this synchronous accessor.
     */
    fun isLoggedIn(): Boolean = _sessionState.value.isLoggedIn
}
