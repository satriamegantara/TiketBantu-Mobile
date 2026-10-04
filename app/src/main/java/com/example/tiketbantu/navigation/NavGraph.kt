package com.example.tiketbantu.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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

/**
 * Root Type-Safe Navigation Graph for TiketBantu Mobile.
 * Connects all screens according to APP_FLOW_TiketBantu.md and features_tiketbantu.md.
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: Screen = Screen.Dashboard,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
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
                onRegisterSuccess = {
                    navController.navigateToDashboardFromAuth()
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
                onNavigateToMyTickets = {
                    navController.safeNavigate(Screen.MyTickets)
                },
                onNavigateToSupported = {
                    navController.safeNavigate(Screen.SupportedTickets)
                },
                onNavigateToCreate = {
                    navController.safeNavigate(Screen.CreateTicket)
                }
            )
        }

        composable<Screen.Monitoring> {
            MonitoringScreen(
                onTicketClick = { ticketId ->
                    navController.safeNavigate(Screen.Detail(ticketId))
                },
                onManageUsersClick = {
                    navController.safeNavigate(Screen.UserManagement(initialTab = 0))
                },
                onManageCategoriesClick = {
                    navController.safeNavigate(Screen.UserManagement(initialTab = 1))
                }
            )
        }

        composable<Screen.UserManagement> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.UserManagement>()
            UserManagementScreen(
                initialTab = route.initialTab,
                onBack = { navController.safePopBackStack() }
            )
        }
    }
}
