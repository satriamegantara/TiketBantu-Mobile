package com.example.tiketbantu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tiketbantu.navigation.NavGraph
import com.example.tiketbantu.navigation.Screen
import com.example.tiketbantu.ui.components.BottomNavigationBar
import com.example.tiketbantu.ui.components.NavRoutes
import com.example.tiketbantu.ui.session.DemoSession
import com.example.tiketbantu.ui.theme.TiketBantuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiketBantuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()

    val selectedNavRoute = when {
        currentRoute.contains("Dashboard") -> NavRoutes.HOME
        currentRoute.contains("MyTickets") -> NavRoutes.MY_TICKETS
        currentRoute.contains("SupportedTickets") -> NavRoutes.SUPPORTED
        currentRoute.contains("CreateTicket") -> NavRoutes.CREATE
        currentRoute.contains("Monitoring") -> NavRoutes.MONITORING
        currentRoute.contains("UserManagement") -> NavRoutes.MANAGE
        currentRoute.contains("Profile") -> NavRoutes.PROFILE
        else -> NavRoutes.HOME
    }

    val showBottomBar = !currentRoute.contains("Login") &&
            !currentRoute.contains("Register") &&
            !currentRoute.contains("TicketDetail") &&
            !currentRoute.contains("CreateTicket")

    Box(modifier = Modifier.fillMaxSize()) {
        NavGraph(
            navController = navController,
            startDestination = Screen.Dashboard,
            modifier = Modifier.fillMaxSize()
        )

        if (showBottomBar) {
            BottomNavigationBar(
                currentRoute = selectedNavRoute,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.HOME -> navController.navigate(Screen.Dashboard) {
                            popUpTo(Screen.Dashboard) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        NavRoutes.MY_TICKETS -> navController.navigate(Screen.MyTickets) {
                            launchSingleTop = true
                        }
                        NavRoutes.CREATE -> navController.navigate(Screen.CreateTicket) {
                            launchSingleTop = true
                        }
                        NavRoutes.SUPPORTED -> navController.navigate(Screen.SupportedTickets) {
                            launchSingleTop = true
                        }
                        NavRoutes.MONITORING -> navController.navigate(Screen.Monitoring) {
                            launchSingleTop = true
                        }
                        NavRoutes.MANAGE -> navController.navigate(Screen.UserManagement) {
                            launchSingleTop = true
                        }
                        NavRoutes.PROFILE -> navController.navigate(Screen.Profile) {
                            launchSingleTop = true
                        }
                    }
                },
                role = DemoSession.role,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    if (org.koin.core.context.GlobalContext.getOrNull() == null) {
        org.koin.core.context.startKoin {
            modules(com.example.tiketbantu.di.appModule)
        }
    }
    TiketBantuTheme {
        MainScreen()
    }
}
