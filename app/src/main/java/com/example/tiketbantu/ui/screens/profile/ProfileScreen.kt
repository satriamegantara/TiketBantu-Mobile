package com.example.tiketbantu.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.example.tiketbantu.data.local.dao.SupportDao
import com.example.tiketbantu.data.local.dao.TicketDao
import com.example.tiketbantu.domain.repository.AuthRepository
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.GlassCard
import com.example.tiketbantu.ui.components.InitialsAvatar
import com.example.tiketbantu.ui.components.TagChip
import com.example.tiketbantu.ui.session.AppRole
import com.example.tiketbantu.ui.session.DemoSession
import com.example.tiketbantu.ui.theme.BrandCyan
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.BrandIndigoSoft
import com.example.tiketbantu.ui.theme.DangerRed
import com.example.tiketbantu.ui.theme.FieldBg
import com.example.tiketbantu.ui.theme.Hairline
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import com.example.tiketbantu.ui.theme.SupportOrange
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Profile Screen: User info, real-time Role Switcher (Pelapor / Agen / Admin),
 * statistics, and settings menus.
 */
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authRepository: AuthRepository = koinInject()
    val ticketDao: TicketDao = koinInject()
    val supportDao: SupportDao = koinInject()
    val scope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }

    val myTickets by ticketDao.observeMine(DemoSession.userId).collectAsState(initial = emptyList())
    val mySupports by supportDao.getSupportedTicketIdsByUser(DemoSession.userId).collectAsState(initial = emptyList())
    val sentCount = myTickets.size
    val doneCount = myTickets.count { it.status.equals("SELESAI", ignoreCase = true) }
    val supportCount = mySupports.size

    AppBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = "Profil Pengguna",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 26.sp),
                        color = Ink
                    )
                    Text(
                        text = "Identitas akun dan preferensi aplikasi",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkMuted
                    )
                }
            }

            item(key = "profile_card") {
                UserProfileCard()
            }

            item(key = "role_switcher") {
                RoleSwitcherCard(
                    onSwitchRole = { targetRole ->
                        scope.launch {
                            when (targetRole) {
                                AppRole.PELAPOR -> authRepository.login("emily.johnson@kampus.ac.id", "password123")
                                AppRole.AGEN -> authRepository.login("joko.santoso@kampus.ac.id", "password123")
                                AppRole.ADMIN -> authRepository.login("admin.sarpras@kampus.ac.id", "password123")
                            }
                        }
                    }
                )
            }

            item(key = "stats") {
                UserStatsCard(
                    sentCount = sentCount,
                    supportCount = supportCount,
                    doneCount = doneCount
                )
            }

            item(key = "menu_section") {
                MenuSettingsCard(onLogoutClick = { showLogoutDialog = true })
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = Color.White,
            title = { Text("Konfirmasi Keluar", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin keluar dari akun TiketBantu?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    scope.launch {
                        authRepository.logout()
                        onLogout()
                    }
                }) {
                    Text("Ya, Keluar", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = InkSoft)
                }
            }
        )
    }
}

@Composable
private fun UserProfileCard() {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                InitialsAvatar(
                    name = DemoSession.name,
                    size = 64.dp,
                    soft = false
                )
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = DemoSession.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = Ink
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = DemoSession.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (badgeBg, badgeFg) = when (DemoSession.role) {
                        AppRole.ADMIN -> Color(0xFFF3E8FF) to Color(0xFF7E22CE)
                        AppRole.AGEN -> BrandIndigoSoft to BrandIndigo
                        AppRole.PELAPOR -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
                    }
                    TagChip(text = DemoSession.role.label, container = badgeBg, content = badgeFg)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "ID: ${DemoSession.nimNip}",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkSoft
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleSwitcherCard(onSwitchRole: (AppRole) -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.SwapHoriz, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Simulasi Peran (Multi-Role Preview)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Ink
                )
                Text(
                    text = "Ganti peran secara instan untuk menguji alur fitur",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = InkMuted
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RolePillOption(
                role = AppRole.PELAPOR,
                title = "Pelapor",
                subtitle = "Mahasiswa",
                selected = DemoSession.role == AppRole.PELAPOR,
                onClick = { onSwitchRole(AppRole.PELAPOR) },
                modifier = Modifier.weight(1f)
            )
            RolePillOption(
                role = AppRole.AGEN,
                title = "Teknisi",
                subtitle = "Pak Joko",
                selected = DemoSession.role == AppRole.AGEN,
                onClick = { onSwitchRole(AppRole.AGEN) },
                modifier = Modifier.weight(1f)
            )
            RolePillOption(
                role = AppRole.ADMIN,
                title = "Admin",
                subtitle = "Sarpras",
                selected = DemoSession.role == AppRole.ADMIN,
                onClick = { onSwitchRole(AppRole.ADMIN) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RolePillOption(
    role: AppRole,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) BrandIndigo else FieldBg,
        border = if (selected) null else BorderStroke(1.dp, Hairline),
        modifier = modifier.height(64.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = if (selected) Color.White else Ink
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = if (selected) Color.White.copy(alpha = 0.8f) else InkMuted
            )
        }
    }
}

@Composable
private fun UserStatsCard(
    sentCount: Int = 0,
    supportCount: Int = 0,
    doneCount: Int = 0
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Aktivitas Kontribusi Kampus",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Ink
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(icon = Icons.Outlined.ConfirmationNumber, count = sentCount.toString(), label = "Aduan Dikirim", color = BrandIndigo)
            StatItem(icon = Icons.Outlined.LocalFireDepartment, count = supportCount.toString(), label = "Dukungan Diberi", color = SupportOrange)
            StatItem(icon = Icons.Outlined.CheckCircle, count = doneCount.toString(), label = "Telah Tuntas", color = Color(0xFF16A34A))
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    count: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = Ink
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = InkMuted
        )
    }
}

@Composable
private fun MenuSettingsCard(onLogoutClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Pengaturan Akun",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Ink
        )
        Spacer(Modifier.height(8.dp))

        MenuItem(icon = Icons.Outlined.Shield, title = "Keamanan Akun & SSO Kampus", onClick = {})
        Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))
        MenuItem(icon = Icons.Outlined.Notifications, title = "Notifikasi & Pembaruan Laporan", onClick = {})
        Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))
        MenuItem(icon = Icons.Outlined.HelpOutline, title = "Pusat Bantuan & FAQ Sarpras", onClick = {})
        Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))
        MenuItem(icon = Icons.Outlined.Info, title = "Tentang TiketBantu Mobile v2.0", onClick = {})
        Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))

        // Logout
        Surface(
            onClick = onLogoutClick,
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = DangerRed, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Keluar Akun",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = DangerRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = InkSoft, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Ink,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = InkMuted)
        }
    }
}
