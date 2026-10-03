package com.example.tiketbantu.base

import com.example.tiketbantu.domain.model.User

/**
 * Immutable snapshot of the current application session state.
 *
 * This is the **single source of truth** for session information shared across
 * all ViewModels via [SessionManager].
 *
 * @param isLoggedIn   Whether a valid session currently exists.
 * @param currentUser  The authenticated [User] object, or null when not logged in.
 * @param isLoading    True while the session is being initialised from DataStore on cold start.
 */
data class SessionState(
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val isLoading: Boolean = true,
) {
    /** Convenience accessor — the role string of the active user, or null if not logged in. */
    val role: String? get() = currentUser?.role

    /** True when the logged-in user has the ADMIN role. */
    val isAdmin: Boolean get() = role == "ADMIN"

    /** True when the logged-in user has the AGEN (agent) role. */
    val isAgen: Boolean get() = role == "AGEN"

    /** True when the logged-in user has the PELAPOR (reporter) role. */
    val isPelapor: Boolean get() = role == "PELAPOR"

    companion object {
        /** Initial state emitted while DataStore hasn't been read yet. */
        val Initial = SessionState(isLoggedIn = false, currentUser = null, isLoading = true)

        /** State to emit after a successful logout. */
        val LoggedOut = SessionState(isLoggedIn = false, currentUser = null, isLoading = false)
    }
}
