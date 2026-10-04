package com.example.tiketbantu.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.automirrored.outlined.ContactSupport
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
import com.example.tiketbantu.ui.theme.Hairline
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import com.example.tiketbantu.ui.theme.SuccessSoftBg
import com.example.tiketbantu.ui.theme.SuccessText
import com.example.tiketbantu.ui.theme.SupportOrange
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tiketbantu.ui.theme.FieldBg
import org.koin.androidx.compose.koinViewModel

/**
 * Profile Screen: User info, real-time Role Switcher (Pelapor / Agen / Admin),
 * statistics, and settings menus.
 */
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToMyTickets: () -> Unit = {},
    onNavigateToSupported: () -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    onMyTicketsClick: () -> Unit = onNavigateToMyTickets,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showGuideDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }

    val name = currentUser?.name?.ifBlank { null } ?: DemoSession.name
    val email = currentUser?.email?.ifBlank { null } ?: DemoSession.email
    val nimNip = currentUser?.nimNip?.ifBlank { null } ?: DemoSession.nimNip
    val role = when (currentUser?.role?.uppercase()) {
        "ADMIN" -> AppRole.ADMIN
        "AGEN" -> AppRole.AGEN
        else -> DemoSession.role
    }

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
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = "Profil",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 26.sp),
                        color = Ink
                    )
                }
            }

            item(key = "profile_card") {
                UserProfileCard(
                    name = name,
                    email = email,
                    nimNip = nimNip,
                    role = role
                )
            }

            item(key = "role_switcher") {
                RoleSwitcherCard(
                    onSwitchRole = { targetRole ->
                        viewModel.switchRoleDemo(targetRole)
                    }
                )
            }

            item(key = "stats") {
                UserStatsCard(
                    sentCount = stats.sentCount,
                    supportCount = stats.supportCount,
                    doneCount = stats.doneCount,
                    onMyTicketsClick = onMyTicketsClick
                )
            }

            item(key = "shortcuts") {
                QuickShortcutsCard(
                    onNavigateToMyTickets = onNavigateToMyTickets,
                    onNavigateToSupported = onNavigateToSupported,
                    onNavigateToCreate = onNavigateToCreate
                )
            }

            item(key = "help_info") {
                HelpAndInfoCard(
                    onOpenGuide = { showGuideDialog = true },
                    onOpenContact = { showContactDialog = true }
                )
            }

            item(key = "logout_section") {
                LogoutCard(onLogoutClick = { showLogoutDialog = true })
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
                    viewModel.logout(onComplete = onLogout)
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

    if (showGuideDialog) {
        GuideDialog(onDismiss = { showGuideDialog = false })
    }

    if (showContactDialog) {
        ContactDialog(onDismiss = { showContactDialog = false })
    }
}

@Composable
private fun UserProfileCard(
    name: String,
    email: String,
    nimNip: String,
    role: AppRole
) {
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
                    name = name,
                    size = 64.dp,
                    soft = false
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = Ink
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (badgeBg, badgeFg) = when (role) {
                        AppRole.ADMIN -> Color(0xFFF3E8FF) to Color(0xFF7E22CE)
                        AppRole.AGEN -> BrandIndigoSoft to BrandIndigo
                        AppRole.PELAPOR -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
                    }
                    TagChip(text = role.label, container = badgeBg, content = badgeFg)
                    if (nimNip.isNotBlank() && nimNip != "-") {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "ID: $nimNip",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkSoft
                        )
                    }
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
            Icon(Icons.Outlined.Sync, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(20.dp))
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
    doneCount: Int = 0,
    onMyTicketsClick: () -> Unit = {}
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
            StatItem(
                icon = Icons.Outlined.ConfirmationNumber,
                count = sentCount.toString(),
                label = "Aduan Dikirim",
                color = BrandIndigo,
                onClick = onMyTicketsClick
            )
            StatItem(
                icon = Icons.Outlined.LocalFireDepartment,
                count = supportCount.toString(),
                label = "Dukungan Diberi",
                color = SupportOrange
            )
            StatItem(
                icon = Icons.Outlined.CheckCircle,
                count = doneCount.toString(),
                label = "Telah Tuntas",
                color = Color(0xFF16A34A)
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    count: String,
    label: String,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        color = Color.Transparent
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
}

@Composable
private fun QuickShortcutsCard(
    onNavigateToMyTickets: (() -> Unit)?,
    onNavigateToSupported: (() -> Unit)?,
    onNavigateToCreate: (() -> Unit)?
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Aksi Cepat & Navigasi",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Ink
        )
        Spacer(Modifier.height(12.dp))

        ShortcutTile(
            icon = Icons.Outlined.ConfirmationNumber,
            title = "Aduan Saya",
            subtitle = "Kelola daftar aduan yang Anda kirimkan",
            badgeColor = BrandIndigoSoft,
            iconColor = BrandIndigo,
            onClick = onNavigateToMyTickets
        )
        Spacer(Modifier.height(8.dp))

        ShortcutTile(
            icon = Icons.Outlined.FavoriteBorder,
            title = "Aduan Saya Dukung",
            subtitle = "Daftar tiket yang Anda beri suara dukungan",
            badgeColor = Color(0xFFFFF1F2),
            iconColor = Color(0xFFE11D48),
            onClick = onNavigateToSupported
        )
        Spacer(Modifier.height(8.dp))

        ShortcutTile(
            icon = Icons.Outlined.AddCircleOutline,
            title = "Buat Aduan Baru",
            subtitle = "Laporkan kerusakan fasilitas kampus baru",
            badgeColor = SuccessSoftBg,
            iconColor = SuccessText,
            onClick = onNavigateToCreate
        )
    }
}

@Composable
private fun ShortcutTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeColor: Color,
    iconColor: Color,
    onClick: (() -> Unit)?
) {
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        shape = RoundedCornerShape(14.dp),
        color = FieldBg,
        border = BorderStroke(1.dp, Hairline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Ink)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = InkMuted)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = InkMuted, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun HelpAndInfoCard(
    onOpenGuide: () -> Unit,
    onOpenContact: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Bantuan & Layanan Kampus",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Ink
        )
        Spacer(Modifier.height(12.dp))

        ShortcutTile(
            icon = Icons.AutoMirrored.Outlined.HelpOutline,
            title = "Panduan & Alur Pelaporan",
            subtitle = "Petunjuk alur pengajuan & penanganan aduan",
            badgeColor = Color(0xFFF3E8FF),
            iconColor = Color(0xFF7E22CE),
            onClick = onOpenGuide
        )
        Spacer(Modifier.height(8.dp))

        ShortcutTile(
            icon = Icons.AutoMirrored.Outlined.ContactSupport,
            title = "Kontak Unit Sarpras",
            subtitle = "Jam operasional kantor & helpdesk pemeliharaan",
            badgeColor = Color(0xFFE0F2FE),
            iconColor = Color(0xFF0369A1),
            onClick = onOpenContact
        )
    }
}


@Composable
private fun LogoutCard(onLogoutClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Surface(
            onClick = onLogoutClick,
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
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
private fun GuideDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null, tint = BrandIndigo)
                Spacer(Modifier.width(8.dp))
                Text("Panduan Pelaporan Aduan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GuideStep(number = "1", title = "Buat Laporan Aduan", desc = "Pilih lokasi gedung/ruangan, sertakan deskripsi kerusakan, dan sertakan foto bukti.")
                GuideStep(number = "2", title = "Berikan Dukungan Suara", desc = "Gunakan tombol heart pada aduan fasilitas publik untuk mempercepat penanganan unit teknisi.")
                GuideStep(number = "3", title = "Penanganan Teknisi Sarpras", desc = "Teknisi Sarpras akan mengklaim tiket, memproses tindakan fisik, dan menandai tiket Selesai.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Mengerti", color = BrandIndigo, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun GuideStep(number: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(BrandIndigoSoft),
            contentAlignment = Alignment.Center
        ) {
            Text(number, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = BrandIndigo)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Ink)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = InkSoft)
        }
    }
}

@Composable
private fun ContactDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Outlined.ContactSupport, contentDescription = null, tint = Color(0xFF0369A1))
                Spacer(Modifier.width(8.dp))
                Text("Kontak Unit Sarpras Kampus", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ContactItem(icon = Icons.Outlined.LocationOn, label = "Kantor Operasional", valText = "Gedung Rektorat Lt. 1 - Unit Pemeliharaan Sarana & Prasarana")
                ContactItem(icon = Icons.Outlined.Schedule, label = "Jam Layanan Teknisi", valText = "Senin - Jumat: 08.00 - 16.00 WIB")
                ContactItem(icon = Icons.Outlined.Info, label = "Email Helpdesk", valText = "helpdesk.sarpras@kampus.ac.id")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = BrandIndigo, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun ContactItem(icon: ImageVector, label: String, valText: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = InkMuted, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = InkMuted)
            Text(valText, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = Ink)
        }
    }
}
