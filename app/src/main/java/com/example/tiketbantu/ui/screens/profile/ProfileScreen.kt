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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import android.widget.Toast
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.tiketbantu.ui.theme.SupportOrange
import org.koin.androidx.compose.koinViewModel

/**
 * Profile Screen: User info, real-time Role Switcher (Pelapor / Agen / Admin),
 * dynamic statistics, entry point to "Aduan Saya", and secure logout (Task 4.5).
 */
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onMyTicketsClick: () -> Unit = {},
    onSupportedTicketsClick: () -> Unit = {},
    onDoneTicketsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showNotifDialog by remember { mutableStateOf(false) }
    var showFaqDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

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
                UserProfileCard(
                    name = name,
                    email = email,
                    nimNip = nimNip,
                    role = role
                )
            }

            item(key = "role_switcher") {
                RoleSwitcherCard(
                    currentRole = role,
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
                    onMyTicketsClick = onMyTicketsClick,
                    onSupportedTicketsClick = onSupportedTicketsClick,
                    onDoneTicketsClick = onDoneTicketsClick
                )
            }

            item(key = "menu_section") {
                MenuSettingsCard(
                    sentCount = stats.sentCount,
                    onMyTicketsClick = onMyTicketsClick,
                    onSecurityClick = { showSecurityDialog = true },
                    onNotifClick = { showNotifDialog = true },
                    onFaqClick = { showFaqDialog = true },
                    onAboutClick = { showAboutDialog = true },
                    onLogoutClick = { showLogoutDialog = true }
                )
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

    if (showSecurityDialog) {
        SecurityDialog(
            name = name,
            email = email,
            nimNip = nimNip,
            onDismiss = { showSecurityDialog = false }
        )
    }

    if (showNotifDialog) {
        NotificationPreferencesDialog(
            onDismiss = { showNotifDialog = false }
        )
    }

    if (showFaqDialog) {
        FaqDialog(
            onDismiss = { showFaqDialog = false }
        )
    }

    if (showAboutDialog) {
        AboutAppDialog(
            onDismiss = { showAboutDialog = false }
        )
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
private fun RoleSwitcherCard(
    currentRole: AppRole,
    onSwitchRole: (AppRole) -> Unit
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
            Icon(
                imageVector = Icons.Outlined.SwapHoriz,
                contentDescription = null,
                tint = BrandIndigo,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Ganti Peran Pengguna (Simulasi)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Ink
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Beralih peran secara instan untuk menguji fitur multi-role kampus",
            style = MaterialTheme.typography.bodySmall,
            color = InkMuted
        )
        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoleOptionChip(
                title = "Pelapor",
                subtitle = "Mahasiswa",
                selected = currentRole == AppRole.PELAPOR,
                onClick = { onSwitchRole(AppRole.PELAPOR) },
                modifier = Modifier.weight(1f)
            )
            RoleOptionChip(
                title = "Agen",
                subtitle = "Teknisi",
                selected = currentRole == AppRole.AGEN,
                onClick = { onSwitchRole(AppRole.AGEN) },
                modifier = Modifier.weight(1f)
            )
            RoleOptionChip(
                title = "Admin",
                subtitle = "Sarpras",
                selected = currentRole == AppRole.ADMIN,
                onClick = { onSwitchRole(AppRole.ADMIN) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RoleOptionChip(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) BrandIndigo else Color.White,
        border = BorderStroke(
            width = if (selected) 0.dp else 1.dp,
            color = if (selected) Color.Transparent else Hairline
        ),
        shadowElevation = if (selected) 4.dp else 0.dp,
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
    onMyTicketsClick: () -> Unit = {},
    onSupportedTicketsClick: () -> Unit = {},
    onDoneTicketsClick: () -> Unit = {}
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
                color = SupportOrange,
                onClick = onSupportedTicketsClick
            )
            StatItem(
                icon = Icons.Outlined.CheckCircle,
                count = doneCount.toString(),
                label = "Telah Tuntas",
                color = Color(0xFF16A34A),
                onClick = onDoneTicketsClick
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
private fun MenuSettingsCard(
    sentCount: Int,
    onMyTicketsClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onNotifClick: () -> Unit,
    onFaqClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Pengaturan Akun & Riwayat",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Ink
        )
        Spacer(Modifier.height(8.dp))

        // Akses navigasi ke Aduan Saya (Sesuai Task 4.5 & DESIGN_TiketBantu baris 173)
        MenuItem(
            icon = Icons.Outlined.ConfirmationNumber,
            title = "Aduan Saya ($sentCount)",
            onClick = onMyTicketsClick
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))

        MenuItem(
            icon = Icons.Outlined.Shield,
            title = "Keamanan Akun & SSO Kampus",
            onClick = onSecurityClick
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))

        MenuItem(
            icon = Icons.Outlined.Notifications,
            title = "Notifikasi & Pembaruan Laporan",
            onClick = onNotifClick
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))

        MenuItem(
            icon = Icons.Outlined.HelpOutline,
            title = "Pusat Bantuan & FAQ Sarpras",
            onClick = onFaqClick
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))

        MenuItem(
            icon = Icons.Outlined.Info,
            title = "Tentang TiketBantu Mobile v2.0",
            onClick = onAboutClick
        )
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

@Composable
private fun SecurityDialog(
    name: String,
    email: String,
    nimNip: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrandIndigoSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Shield, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Keamanan Akun & SSO", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Akun Anda terhubung langsung dengan sistem Single Sign-On (SSO) Kampus.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Hairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("• Nama: $name", style = MaterialTheme.typography.bodySmall, color = Ink)
                        Text("• Email SSO: $email", style = MaterialTheme.typography.bodySmall, color = Ink)
                        Text("• Identitas: $nimNip", style = MaterialTheme.typography.bodySmall, color = Ink)
                        Text("• Enkripsi: Password hash lokal tersimpan aman", style = MaterialTheme.typography.bodySmall, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                    }
                }
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
private fun NotificationPreferencesDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var statusNotif by remember { mutableStateOf(true) }
    var commentNotif by remember { mutableStateOf(true) }
    var toastFeedback by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Preferensi Notifikasi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Atur preferensi pemberitahuan laporan sarpras di perangkat ini:",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted
                )
                PreferenceSwitchRow(
                    title = "Pembaruan Status Tiket",
                    subtitle = "Notifikasi saat tiket berubah ke Diproses / Selesai",
                    checked = statusNotif,
                    onCheckedChange = { statusNotif = it }
                )
                PreferenceSwitchRow(
                    title = "Tanggapan / Komentar Agen",
                    subtitle = "Pemberitahuan saat teknisi menulis update tindak lanjut",
                    checked = commentNotif,
                    onCheckedChange = { commentNotif = it }
                )
                PreferenceSwitchRow(
                    title = "Toast Feedback di Layar",
                    subtitle = "Umpan balik instan setiap aksi berhasil",
                    checked = toastFeedback,
                    onCheckedChange = { toastFeedback = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                Toast.makeText(context, "Preferensi notifikasi berhasil disimpan", Toast.LENGTH_SHORT).show()
                onDismiss()
            }) {
                Text("Simpan", color = BrandIndigo, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = InkSoft)
            }
        }
    )
}

@Composable
private fun PreferenceSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Ink)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = InkMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BrandIndigo,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Hairline
            )
        )
    }
}

@Composable
private fun FaqDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE0F7FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.HelpOutline, contentDescription = null, tint = Color(0xFF00838F), modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Pusat Bantuan & FAQ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FaqItem(
                    q = "Cara membuat aduan fasilitas?",
                    a = "Tekan tombol '+' di menu bawah, lengkapi lokasi gedung/ruangan dan lampirkan 1 foto bukti kendala."
                )
                FaqItem(
                    q = "Apa itu 'Saya Juga Mengalami'?",
                    a = "Fitur solidaritas warga kampus. Semakin banyak dukungan suara, prioritas penanganan semakin tinggi."
                )
                FaqItem(
                    q = "Berapa lama laporan ditangani?",
                    a = "Teknisi sarpras biasanya mengklaim aduan dalam 1x24 jam kerja sesuai ketersediaan sparepart."
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Text(
                        text = "Kontak Operasional: sarpras@kampus.ac.id",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = InkSoft,
                        modifier = Modifier.padding(8.dp)
                    )
                }
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
private fun FaqItem(q: String, a: String) {
    Column {
        Text(text = "Q: $q", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Ink)
        Text(text = a, style = MaterialTheme.typography.labelSmall, color = InkSoft)
    }
}

@Composable
private fun AboutAppDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrandIndigoSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Tentang TiketBantu", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TiketBantu Mobile v2.0.0",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Ink
                )
                Text(
                    text = "Sistem pelaporan fasilitas sarana & prasarana kampus terpadu berbasis transparansi publik dan urgensi suara solidaritas mahasiswa.",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSoft
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Teknologi: Jetpack Compose, Material 3, Room Local DB, Koin Dependency Injection.",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkMuted
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = BrandIndigo, fontWeight = FontWeight.Bold)
            }
        }
    )
}
