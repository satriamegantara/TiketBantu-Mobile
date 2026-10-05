package com.example.tiketbantu.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.GlassCard
import com.example.tiketbantu.ui.components.TagChip
import com.example.tiketbantu.ui.session.AppRole
import com.example.tiketbantu.ui.session.DemoSession
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.BrandIndigoSoft
import com.example.tiketbantu.ui.theme.DangerRed
import com.example.tiketbantu.ui.theme.Hairline
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import com.example.tiketbantu.ui.theme.SuccessSoftBg
import com.example.tiketbantu.ui.theme.SuccessText
import org.koin.androidx.compose.koinViewModel

/**
 * Profile Screen:
 * - Standard top page title matching other screens.
 * - Circular avatar and User Name centered.
 * - User details card (NIM/ID, Email, Role, Status) displayed above.
 * - Activity summary section ("My documents" style) with 3 clickable cards below:
 *   Aduan Dikirim, Didukung, and Telah Tuntas.
 * - Secure logout action.
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

    val name = currentUser?.name?.ifBlank { null } ?: DemoSession.name
    val email = currentUser?.email?.ifBlank { null } ?: DemoSession.email
    val nimNip = currentUser?.nimNip?.ifBlank { null } ?: DemoSession.nimNip
    val role = when (currentUser?.role?.uppercase()) {
        "ADMIN" -> AppRole.ADMIN
        "AGEN" -> AppRole.AGEN
        "PELAPOR" -> AppRole.PELAPOR
        else -> DemoSession.role
    }

    AppBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Standard Top Bar Title (Samakan dengan page lain)
            item(key = "header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = "Profil",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp
                        ),
                        color = Ink
                    )
                }
            }

            // 2. Avatar & Name (Centered)
            item(key = "avatar_and_name") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                                spotColor = BrandIndigo.copy(alpha = 0.2f)
                            )
                            .background(Color.White, CircleShape)
                            .padding(3.dp)
                            .border(1.5.dp, BrandIndigo.copy(alpha = 0.3f), CircleShape)
                            .clip(CircleShape)
                            .background(BrandIndigoSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Avatar Pengguna",
                            tint = BrandIndigo,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        ),
                        color = Ink,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 3. User Details Card (Nama, Email, ID, Role di atas)
            item(key = "user_details_card") {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ProfileInfoRow(
                            icon = Icons.Outlined.Badge,
                            label = "NOMOR INDUK (NIM / NIP)",
                            value = if (nimNip.isNotBlank() && nimNip != "-") nimNip else "Belum diatur"
                        )

                        HorizontalDivider(
                            color = Hairline,
                            modifier = Modifier.padding(start = 48.dp, top = 10.dp, bottom = 10.dp)
                        )

                        ProfileInfoRow(
                            icon = Icons.Outlined.Email,
                            label = "EMAIL RESMI",
                            value = email
                        )

                        HorizontalDivider(
                            color = Hairline,
                            modifier = Modifier.padding(start = 48.dp, top = 10.dp, bottom = 10.dp)
                        )

                        ProfileInfoRow(
                            icon = Icons.Outlined.Person,
                            label = "PERAN PENGGUNA",
                            customContent = {
                                val (badgeBg, badgeFg) = when (role) {
                                    AppRole.ADMIN -> Color(0xFFF3E8FF) to Color(0xFF7E22CE)
                                    AppRole.AGEN -> BrandIndigoSoft to BrandIndigo
                                    AppRole.PELAPOR -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
                                }
                                TagChip(text = role.label, container = badgeBg, content = badgeFg)
                            }
                        )

                        HorizontalDivider(
                            color = Hairline,
                            modifier = Modifier.padding(start = 48.dp, top = 10.dp, bottom = 10.dp)
                        )

                        ProfileInfoRow(
                            icon = Icons.Outlined.CheckCircle,
                            iconTint = SuccessText,
                            iconBg = SuccessSoftBg,
                            label = "STATUS AKUN",
                            value = "Aktif (Terverifikasi)",
                            valueColor = SuccessText
                        )
                    }
                }
            }

            // 4. Section Header & Activity Cards (Hanya untuk Agen & Pelapor, Admin = Pure Monitoring)
            if (role != AppRole.ADMIN) {
                item(key = "activity_section_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (role == AppRole.AGEN) "Aktivitas Penanganan" else "Aktivitas Aduan",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = Ink
                        )

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = InkMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 5. Activity Row (Agen: Tugas & Telah Tuntas; Pelapor: Aduan Dikirim, Didukung, Telah Tuntas)
                item(key = "activity_cards_row") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ActivityDocCard(
                            modifier = Modifier.weight(1f),
                            icon = if (role == AppRole.AGEN) Icons.AutoMirrored.Outlined.Assignment else Icons.Outlined.ConfirmationNumber,
                            title = if (role == AppRole.AGEN) "Tugas" else "Aduan Dikirim",
                            count = "${stats.sentCount} tiket",
                            iconTint = BrandIndigo,
                            iconBg = BrandIndigoSoft,
                            onClick = {
                                DemoSession.myTicketsInitialStatus = null
                                onMyTicketsClick()
                            }
                        )

                        if (role != AppRole.AGEN) {
                            ActivityDocCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Outlined.FavoriteBorder,
                                title = "Didukung",
                                count = "${stats.supportCount} aduan",
                                iconTint = Color(0xFFE11D48),
                                iconBg = Color(0xFFFEE2E2),
                                onClick = onNavigateToSupported
                            )
                        }

                        ActivityDocCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.CheckCircle,
                            title = "Telah Tuntas",
                            count = "${stats.doneCount} selesai",
                            iconTint = Color(0xFF16A34A),
                            iconBg = Color(0xFFDCFCE7),
                            onClick = {
                                DemoSession.myTicketsInitialStatus = TicketStatus.SELESAI
                                onMyTicketsClick()
                            }
                        )
                    }
                }
            }

            // 6. Logout Section
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
}

/**
 * Single detail item in the User Info card.
 */
@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String = "",
    valueColor: Color = Ink,
    iconTint: Color = BrandIndigo,
    iconBg: Color = BrandIndigoSoft,
    customContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = InkMuted
            )
            Spacer(Modifier.height(2.dp))
            if (customContent != null) {
                customContent()
            } else {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = valueColor
                    )
                )
            }
        }
    }
}

/**
 * 3-Card Activity item inspired by "My documents" in reference design.
 */
@Composable
private fun ActivityDocCard(
    icon: ImageVector,
    title: String,
    count: String,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Hairline),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = count,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = InkMuted,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LogoutCard(onLogoutClick: () -> Unit) {
    Surface(
        onClick = onLogoutClick,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFEF2F2),
        border = BorderStroke(1.dp, Color(0xFFFEE2E2)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .height(50.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = DangerRed,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Keluar Akun",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = DangerRed
            )
        }
    }
}