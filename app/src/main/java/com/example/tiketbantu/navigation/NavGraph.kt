package com.example.tiketbantu.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute


/**
 * Root Navigation Graph for TiketBantu Mobile.
 * Connects all screens according to APP_FLOW_TiketBantu.md.
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: Screen = Screen.Dashboard
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Screen.Login> {
            PlaceholderScreen(title = "Halaman Login")
        }
        composable<Screen.Register> {
            PlaceholderScreen(title = "Halaman Registrasi")
        }

        composable<Screen.Dashboard> {
            FeedScreen(onTicketClick = { navController.navigate(Screen.TicketDetail(it)) })
        }

        composable<Screen.CreateTicket> {
            PlaceholderScreen(title = "Form Buat Aduan Baru")
        }
        composable<Screen.TicketDetail> { entry ->
            val route = entry.toRoute<Screen.TicketDetail>()
            TicketDetailScreen(ticketId = route.ticketId, onBack = { navController.popBackStack() })
        }

        composable<Screen.MyTickets> {
            PlaceholderScreen(title = "Aduan Saya")
        }
        composable<Screen.Profile> {
            PlaceholderScreen(title = "Profil Pengguna")
        }
        composable<Screen.Monitoring> {
            PlaceholderScreen(title = "Pure Monitoring Dashboard (Admin)")
        }
        composable<Screen.UserManagement> {
            PlaceholderScreen(title = "Manajemen Pengguna & Kategori (Admin)")
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}
