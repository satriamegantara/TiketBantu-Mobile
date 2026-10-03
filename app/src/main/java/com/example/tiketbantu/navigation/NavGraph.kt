package com.example.tiketbantu.navigation

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
 * Root Navigation Graph for TiketBantu Mobile.
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
        modifier = modifier
    ) {
        composable<Screen.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register)
                }
            )
        }

        composable<Screen.Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.Dashboard> {
            DashboardScreen(
                onTicketClick = { ticketId ->
                    navController.navigate(Screen.TicketDetail(ticketId))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile)
                }
            )
        }

        composable<Screen.CreateTicket> {
            CreateTicketScreen(
                onCancel = { navController.popBackStack() },
                onCreated = { ticketId ->
                    navController.navigate(Screen.TicketDetail(ticketId)) {
                        popUpTo(Screen.Dashboard)
                    }
                }
            )
        }

        composable<Screen.TicketDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.TicketDetail>()
            TicketDetailScreen(
                ticketId = route.ticketId,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Screen.MyTickets> {
            MyTicketsScreen(
                onTicketClick = { ticketId ->
                    navController.navigate(Screen.TicketDetail(ticketId))
                }
            )
        }

        composable<Screen.SupportedTickets> {
            SupportedTicketsScreen(
                onTicketClick = { ticketId ->
                    navController.navigate(Screen.TicketDetail(ticketId))
                }
            )
        }

        composable<Screen.Profile> {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Monitoring> {
            MonitoringScreen(
                onTicketClick = { ticketId ->
                    navController.navigate(Screen.TicketDetail(ticketId))
                },
                onManageUsersClick = {
                    navController.navigate(Screen.UserManagement)
                },
                onManageCategoriesClick = {
                    navController.navigate(Screen.UserManagement)
                }
            )
        }

        composable<Screen.UserManagement> {
            UserManagementScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
