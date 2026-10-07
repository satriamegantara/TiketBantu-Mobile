package com.example.tiketbantu.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder

/**
 * Anti-Crash navigation extensions for Jetpack Compose Navigation in TiketBantu Mobile.
 *
 * Prevents common runtime exceptions and UX glitches:
 * 1. Rapid double-tapping on buttons triggering duplicate screen push or crashing the stack.
 * 2. Navigating during pending transitions when lifecycle is not in [Lifecycle.State.RESUMED].
 * 3. Backstack pollution after authentication (clearing Login/Register upon reaching Dashboard).
 * 4. Leaving stale screens in the stack when the user logs out.
 */

/**
 * Safely navigates to a target [route] only when the current backstack entry is in [Lifecycle.State.RESUMED].
 * Automatically enables [NavOptionsBuilder.launchSingleTop] = true to avoid duplicate screens.
 *
 * @param route The @Serializable [Screen] target destination.
 * @param builder Optional DSL block for additional NavOptions (e.g. popUpTo).
 */
fun NavController.safeNavigate(
    route: Screen,
    builder: (NavOptionsBuilder.() -> Unit)? = null
) {
    val currentEntry = currentBackStackEntry
    // Prevent double-click crashes by ensuring the current entry is fully resumed
    val isResumed = currentEntry == null || currentEntry.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    if (isResumed) {
        try {
            navigate(route) {
                launchSingleTop = true
                builder?.invoke(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Safely pops the backstack only when the current backstack entry is in [Lifecycle.State.RESUMED].
 * Prevents rapid double-tap back press crashes.
 *
 * @return True if the stack was popped, false otherwise.
 */
fun NavController.safePopBackStack(): Boolean {
    val currentEntry = currentBackStackEntry
    val isResumed = currentEntry == null || currentEntry.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    return if (isResumed) {
        try {
            popBackStack()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    } else {
        false
    }
}

/**
 * Navigates to [Screen.Dashboard] and clears the authentication backstack (Login & Register).
 */
fun NavController.navigateToDashboardFromAuth() {
    safeNavigate(Screen.Dashboard) {
        popUpTo<Screen.Login> {
            inclusive = true
        }
        launchSingleTop = true
    }
}

/**
 * Navigates to [Screen.Login] and clears the entire backstack upon user logout.
 */
fun NavController.navigateToLoginFromLogout() {
    safeNavigate(Screen.Login) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
