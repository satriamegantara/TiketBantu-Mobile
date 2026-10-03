package com.example.tiketbantu.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

/**
 * Root Type-Safe Navigation Graph for TiketBantu Mobile.
 *
 * Implements:
 * - Jetpack Compose Navigation with Kotlinx Serialization Type-Safe destinations:
 *   [Screen.Login], [Screen.Register], [Screen.Dashboard], [Screen.CreateTicket],
 *   [Screen.Detail], [Screen.MyTickets], [Screen.Profile], [Screen.Monitoring], [Screen.UserManagement].
 * - Smooth Material 3 enter/exit transitions (fade + slide).
 * - Anti-crash guardrails via [safeNavigate] and [safePopBackStack].
 * - Clean backstack management for authentication and session lifecycle.
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
        // ── 1. Login Screen ──────────────────────────────────────────────────
        composable<Screen.Login> {
            NavDestinationScreen(
                title = "Halaman Login",
                subtitle = "Autentikasi multi-role lokal untuk Pelapor, Agen, dan Admin",
                badge = "Auth Graph",
                badgeColor = MaterialTheme.colorScheme.primary,
                actions = listOf(
                    NavAction("Masuk ke Dashboard (Login)") {
                        navController.navigateToDashboardFromAuth()
                    },
                    NavAction("Daftar Akun Baru (Register)", isOutlined = true) {
                        navController.safeNavigate(Screen.Register)
                    }
                )
            )
        }

        // ── 2. Register Screen ───────────────────────────────────────────────
        composable<Screen.Register> {
            NavDestinationScreen(
                title = "Halaman Registrasi",
                subtitle = "Pendaftaran akun pelapor lokal (NIM/NIP, email, password)",
                badge = "Auth Graph",
                badgeColor = MaterialTheme.colorScheme.primary,
                onBack = { navController.safePopBackStack() },
                actions = listOf(
                    NavAction("Daftar & Kembali ke Login") {
                        navController.safeNavigate(Screen.Login) {
                            popUpTo<Screen.Register> { inclusive = true }
                        }
                    },
                    NavAction("Sudah Punya Akun? Login", isOutlined = true) {
                        navController.safePopBackStack()
                    }
                )
            )
        }

        // ── 3. Dashboard / Feed Screen ───────────────────────────────────────
        composable<Screen.Dashboard> {
            NavDestinationScreen(
                title = "Dashboard & Feed Aduan",
                subtitle = "Feed aduan publik kampus dengan LazyColumn & sorting Most Liked",
                badge = "Main Graph",
                badgeColor = MaterialTheme.colorScheme.primary,
                actions = listOf(
                    NavAction("➕ Buat Aduan Baru") {
                        navController.safeNavigate(Screen.CreateTicket)
                    },
                    NavAction("🔍 Lihat Detail Aduan #1 (AC Lab SI)") {
                        navController.safeNavigate(Screen.Detail(1L))
                    },
                    NavAction("🔍 Lihat Detail Aduan #2 (Proyektor)") {
                        navController.safeNavigate(Screen.Detail(2L))
                    },
                    NavAction("📋 Aduan Saya", isOutlined = true) {
                        navController.safeNavigate(Screen.MyTickets)
                    },
                    NavAction("👤 Profil Pengguna", isOutlined = true) {
                        navController.safeNavigate(Screen.Profile)
                    },
                    NavAction("📊 Pure Monitoring Dashboard (Admin)", isOutlined = true) {
                        navController.safeNavigate(Screen.Monitoring)
                    }
                )
            )
        }

        // ── 4. Create Ticket Screen ──────────────────────────────────────────
        composable<Screen.CreateTicket> {
            NavDestinationScreen(
                title = "Form Buat Aduan Baru",
                subtitle = "Input judul, kategori, lokasi gedung/lantai/ruang, & 1 foto bukti",
                badge = "Main Graph",
                badgeColor = MaterialTheme.colorScheme.secondary,
                onBack = { navController.safePopBackStack() },
                actions = listOf(
                    NavAction("💾 Simpan & Buka Detail Aduan (#101)") {
                        navController.safeNavigate(Screen.Detail(101L)) {
                            popUpTo<Screen.CreateTicket> { inclusive = true }
                        }
                    },
                    NavAction("Batal / Kembali", isOutlined = true) {
                        navController.safePopBackStack()
                    }
                )
            )
        }

        // ── 5. Detail Screen (Detail/{id}) ───────────────────────────────────
        composable<Screen.Detail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.Detail>()
            val ticketId = route.id
            NavDestinationScreen(
                title = "Detail Aduan #$ticketId",
                subtitle = "Informasi lengkap, riwayat status, foto terlampir, thread komentar & polling",
                badge = "Route: Detail/{id} → id=$ticketId",
                badgeColor = MaterialTheme.colorScheme.tertiary,
                onBack = { navController.safePopBackStack() },
                actions = listOf(
                    NavAction("← Kembali ke Feed") {
                        navController.safePopBackStack()
                    },
                    NavAction("❤️ Simulasi Dukungan 'Saya Juga Mengalami'", isOutlined = true) {
                        // Interactive action verification
                    }
                )
            )
        }

        // ── 6. My Tickets Screen ─────────────────────────────────────────────
        composable<Screen.MyTickets> {
            NavDestinationScreen(
                title = "Aduan Saya",
                subtitle = "Riwayat aduan yang diajukan oleh pengguna aktif yang sedang login",
                badge = "Personal",
                badgeColor = MaterialTheme.colorScheme.secondary,
                onBack = { navController.safePopBackStack() },
                actions = listOf(
                    NavAction("Lihat Detail Aduan #1") {
                        navController.safeNavigate(Screen.Detail(1L))
                    },
                    NavAction("← Kembali", isOutlined = true) {
                        navController.safePopBackStack()
                    }
                )
            )
        }

        // ── 7. Profile Screen ────────────────────────────────────────────────
        composable<Screen.Profile> {
            NavDestinationScreen(
                title = "Profil Pengguna",
                subtitle = "Informasi akun (Pelapor / Agen / Admin) dan manajemen sesi login",
                badge = "User Profile",
                badgeColor = MaterialTheme.colorScheme.primary,
                onBack = { navController.safePopBackStack() },
                actions = listOf(
                    NavAction("📋 Buka Aduan Saya") {
                        navController.safeNavigate(Screen.MyTickets)
                    },
                    NavAction("🚪 Logout (Keluar ke Login)") {
                        navController.navigateToLoginFromLogout()
                    },
                    NavAction("← Kembali ke Dashboard", isOutlined = true) {
                        navController.safePopBackStack()
                    }
                )
            )
        }

        // ── 8. Monitoring Screen (Admin) ─────────────────────────────────────
        composable<Screen.Monitoring> {
            NavDestinationScreen(
                title = "Pure Monitoring Dashboard",
                subtitle = "Monitoring agregat status aduan (Baru, Diproses, Selesai) untuk Admin",
                badge = "Admin Only",
                badgeColor = MaterialTheme.colorScheme.error,
                onBack = { navController.safePopBackStack() },
                actions = listOf(
                    NavAction("👥 Kelola Pengguna & Kategori") {
                        navController.safeNavigate(Screen.UserManagement)
                    },
                    NavAction("🔍 Periksa Aduan #1 dari Monitoring") {
                        navController.safeNavigate(Screen.Detail(1L))
                    },
                    NavAction("← Kembali ke Dashboard", isOutlined = true) {
                        navController.safePopBackStack()
                    }
                )
            )
        }

        // ── 9. User Management Screen (Admin) ────────────────────────────────
        composable<Screen.UserManagement> {
            NavDestinationScreen(
                title = "Manajemen Pengguna & Kategori",
                subtitle = "Pengelolaan daftar akun, role pengguna, dan kategori aduan lokal",
                badge = "Admin Only",
                badgeColor = MaterialTheme.colorScheme.error,
                onBack = { navController.safePopBackStack() },
                actions = listOf(
                    NavAction("← Kembali ke Monitoring Dashboard") {
                        navController.safePopBackStack()
                    }
                )
            )
        }
    }
}

/**
 * Data holder for interactive navigation test actions.
 */
data class NavAction(
    val label: String,
    val isOutlined: Boolean = false,
    val onClick: () -> Unit
)

/**
 * Standard Material 3 destination container used by [NavGraph].
 * Provides a clean header, status chip, explanation card, and test action buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavDestinationScreen(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    onBack: (() -> Unit)? = null,
    actions: List<NavAction> = emptyList()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.padding(start = 8.dp),
                            contentPadding = ButtonDefaults.TextButtonContentPadding
                        ) {
                            Text(text = "←", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Badge
            Surface(
                shape = CircleShape,
                color = badgeColor.copy(alpha = 0.15f),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = badge,
                    color = badgeColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Interactive Navigation Actions
            if (actions.isNotEmpty()) {
                Text(
                    text = "Aksi Navigasi (Type-Safe):",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                actions.forEach { action ->
                    if (action.isOutlined) {
                        OutlinedButton(
                            onClick = action.onClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = action.label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Button(
                            onClick = action.onClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = action.label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
