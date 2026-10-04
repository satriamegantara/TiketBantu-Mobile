package com.example.tiketbantu.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.tiketbantu.data.preferences.SessionManager
import com.example.tiketbantu.ui.screens.admin.UserManagementScreen
import com.example.tiketbantu.ui.screens.auth.LoginScreen
import com.example.tiketbantu.ui.screens.auth.RegisterScreen
import com.example.tiketbantu.ui.screens.create.CreateTicketScreen
import com.example.tiketbantu.ui.screens.dashboard.DashboardScreen
import com.example.tiketbantu.ui.screens.detail.TicketDetailScreen
import com.example.tiketbantu.ui.screens.monitoring.MonitoringScreen
import com.example.tiketbantu.ui.screens.mytickets.MyTicketsScreen
import com.example.tiketbantu.ui.screens.profile.ProfileScreen
import com.example.tiketbantu.ui.screens.supported.SupportedTicketsScreen
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.ui.session.AppRole
import com.example.tiketbantu.ui.session.DemoSession
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Root Type-Safe Navigation Graph for TiketBantu Mobile.
 * Connects all screens according to APP_FLOW_TiketBantu.md and features_tiketbantu.md.
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    sessionManager: SessionManager = koinInject(),
    startDestination: Screen? = null,
    modifier: Modifier = Modifier
) {
    val sessionState by sessionManager.sessionState.collectAsState()

    if (sessionState.isLoading && startDestination == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Sinkronisasi DemoSession pada cold-start jika sesi aktif ditemukan
        if (sessionState.isLoggedIn && sessionState.currentUser != null) {
            val user = sessionState.currentUser!!
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

        val resolvedStart = remember {
            startDestination ?: if (sessionState.isLoggedIn) Screen.Dashboard else Screen.Login
        }

        NavHost(
            navController = navController,
            startDestination = resolvedStart,
            modifier = modifier,
            enterTransition = {
                fadeIn(animationSpec = tween(250)) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(250)) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(250)
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(250)) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(250)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(250)) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(250)
                )
            }
        ) {
            composable<Screen.Login> {
                LoginScreen(
                    viewModel = koinViewModel(),
                    onLoginSuccess = {
                        navController.navigateToDashboardFromAuth()
                    },
                    onNavigateToRegister = {
                        navController.safeNavigate(Screen.Register)
                    }
                )
            }

            composable<Screen.Register> {
                RegisterScreen(
                    viewModel = koinViewModel(),
                    onRegisterSuccess = {
                        navController.safeNavigate(Screen.Login) {
                            popUpTo<Screen.Register> { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.safePopBackStack()
                    }
                )
            }

            composable<Screen.Dashboard> {
                DashboardScreen(
                    onTicketClick = { ticketId ->
                        navController.safeNavigate(Screen.Detail(ticketId))
                    },
                    onProfileClick = {
                        navController.safeNavigate(Screen.Profile)
                    }
                )
            }

            composable<Screen.CreateTicket> {
                CreateTicketScreen(
                    onCancel = { navController.safePopBackStack() },
                    onCreated = { ticketId ->
                        navController.safeNavigate(Screen.Detail(ticketId)) {
                            popUpTo<Screen.Dashboard> { inclusive = false }
                        }
                    }
                )
            }

            composable<Screen.Detail> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.Detail>()
                TicketDetailScreen(
                    ticketId = route.id,
                    onBack = { navController.safePopBackStack() }
                )
            }

            composable<Screen.MyTickets> {
                MyTicketsScreen(
                    onTicketClick = { ticketId ->
                        navController.safeNavigate(Screen.Detail(ticketId))
                    },
                    onBack = {
                        navController.safePopBackStack()
                    }
                )
            }

            composable<Screen.SupportedTickets> {
                SupportedTicketsScreen(
                    onTicketClick = { ticketId ->
                        navController.safeNavigate(Screen.Detail(ticketId))
                    }
                )
            }

            composable<Screen.Profile> {
                ProfileScreen(
                    onLogout = {
                        navController.navigateToLoginFromLogout()
                    },
                    onMyTicketsClick = {
                        navController.safeNavigate(Screen.MyTickets)
                    },
                    onSupportedTicketsClick = {
                        navController.safeNavigate(Screen.SupportedTickets)
                    }
                )
            }

        composable<Screen.Monitoring> {
            MonitoringScreen(
                onTicketClick = { ticketId ->
                    navController.safeNavigate(Screen.Detail(ticketId))
                },
                onManageUsersClick = {
                    navController.safeNavigate(Screen.UserManagement)
                },
                onManageCategoriesClick = {
                    navController.safeNavigate(Screen.UserManagement)
                }
            )
        }

        composable<Screen.UserManagement> {
            UserManagementScreen(
                onBack = { navController.safePopBackStack() }
            )
        }
    }
}
