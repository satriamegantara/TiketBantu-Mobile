package com.example.tiketbantu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.ui.session.AppRole
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.BrandIndigoSoft
import com.example.tiketbantu.ui.theme.ChipProsesFg
import com.example.tiketbantu.ui.theme.FieldBg
import com.example.tiketbantu.ui.theme.Hairline
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import com.example.tiketbantu.ui.theme.SuccessSoftBg
import com.example.tiketbantu.ui.theme.SuccessText
import com.example.tiketbantu.ui.theme.TiketBantuTheme
import java.io.File

fun Ticket.locationText(): String =
    listOfNotNull(
        locationBuilding.takeIf { it.isNotBlank() },
        locationFloor.takeIf { it.isNotBlank() }?.let { "Lt. $it" },
        locationRoom.takeIf { it.isNotBlank() }
    ).joinToString(" • ")

/**
 * Public feed card (feature 2.2 / 2.6 / 3.3.1).
 * Category tag + status pill, location, title, description, optional photo, handler box
 * (claim for Agen, assignee, or "dituntaskan" note) and the orange support pill.
 */
@Composable
fun TicketCard(
    ticket: Ticket,
    onClick: () -> Unit,
    onToggleSupport: () -> Unit,
    modifier: Modifier = Modifier,
    role: AppRole = AppRole.PELAPOR,
    onClaimClick: (() -> Unit)? = null,
    commentCount: Int = 0
) {
    val finished = TicketStatus.isFinished(ticket.status)

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        containerColor = if (finished) Color.White.copy(alpha = 0.92f) else Color.White
    ) {
        // Tag + status
        Row(verticalAlignment = Alignment.CenterVertically) {
            TagChip(ticket.categoryName.ifBlank { "Umum" })
            Spacer(Modifier.weight(1f))
            StatusPill(ticket.status)
        }
        Spacer(Modifier.height(10.dp))
        MetaRow(Icons.Outlined.Place, ticket.locationText())
        Spacer(Modifier.height(6.dp))
        Text(
            text = ticket.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, lineHeight = 24.sp),
            color = Ink,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = ticket.description,
            style = MaterialTheme.typography.bodyMedium,
            color = InkSoft,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        ticket.imageUrl?.takeIf { it.isNotBlank() }?.let { path ->
            Spacer(Modifier.height(12.dp))
            Box {
                AsyncImage(
                    model = if (path.startsWith("content:") || path.startsWith("file:")) path else File(path),
                    contentDescription = "Foto bukti",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(FieldBg)
                )
                TagChip(
                    text = "1 Foto Bukti",
                    icon = Icons.Outlined.PhotoCamera,
                    container = Color.Black.copy(alpha = 0.55f),
                    content = Color.White,
                    modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        HandlerBox(ticket = ticket, role = role, onClaimClick = onClaimClick)
        Spacer(Modifier.height(12.dp))

        // Footer
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.AccessTime, null, tint = InkMuted, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(relativeTime(ticket.createdAt), style = MaterialTheme.typography.bodySmall, color = InkMuted)
            Spacer(Modifier.weight(1f))
            if (ticket.status == TicketStatus.SELESAI) {
                Icon(Icons.Filled.CheckCircle, null, tint = SuccessText, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    "${ticket.supportCount} Civitas Terbantu",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = SuccessText
                )
            } else {
                SupportPill(
                    count = ticket.supportCount,
                    supported = ticket.isSupportedByMe,
                    onClick = onToggleSupport,
                    enabled = !finished
                )
            }
            Spacer(Modifier.width(12.dp))
            Icon(Icons.AutoMirrored.Outlined.Chat, null, tint = InkSoft, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("$commentCount", style = MaterialTheme.typography.labelLarge, color = InkSoft)
        }
    }
}

@Composable
private fun HandlerBox(ticket: Ticket, role: AppRole, onClaimClick: (() -> Unit)?) {
    val shape = RoundedCornerShape(14.dp)
    when {
        ticket.status == TicketStatus.SELESAI -> {
            Surface(shape = shape, color = SuccessSoftBg, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Aduan Telah Dituntaskan", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = SuccessText)
                    Text(
                        "Ditangani oleh ${ticket.agentName ?: "Teknisi Sarpras"}. Tiket terkunci dari pengeditan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SuccessText.copy(alpha = 0.85f)
                    )
                }
            }
        }
        ticket.status == TicketStatus.DITUTUP -> {
            Surface(shape = shape, color = FieldBg, border = BorderStroke(1.dp, Hairline), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Lock, null, tint = InkMuted, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Aduan ditutup & terkunci", style = MaterialTheme.typography.bodySmall, color = InkSoft)
                }
            }
        }
        ticket.status == TicketStatus.DIPROSES || ticket.agentName != null -> {
            val agent = ticket.agentName ?: "Teknisi Sarpras"
            Surface(shape = shape, color = BrandIndigoSoft.copy(alpha = 0.6f), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    InitialsAvatar(agent, size = 30.dp, soft = false)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Sedang ditangani oleh: $agent (Teknisi Sarpras)",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ChipProsesFg,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Filled.Engineering, null, tint = ChipProsesFg, modifier = Modifier.size(18.dp))
                }
            }
        }
        else -> {
            Surface(shape = shape, color = FieldBg, border = BorderStroke(1.dp, Hairline), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.PersonSearch, null, tint = InkMuted, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Belum ada teknisi ditugaskan",
                        style = MaterialTheme.typography.bodySmall,
                        color = InkSoft,
                        modifier = Modifier.weight(1f)
                    )
                    if (role == AppRole.AGEN && onClaimClick != null) {
                        Surface(
                            onClick = onClaimClick,
                            shape = RoundedCornerShape(50),
                            color = BrandIndigo
                        ) {
                            Text(
                                "Klaim Tiket",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F6FB)
@Composable
private fun TicketCardPreview() {
    val base = Ticket(
        id = 89, title = "AC Lab Komputer 302 Mati Total & Bocor Air Membasahi Meja PC",
        description = "Suhu ruangan sangat panas mengganggu praktikum Algoritma, dan tetesan air merembes.",
        categoryId = 2, categoryName = "Fasilitas Ruangan",
        locationBuilding = "Gedung Thomas Aquinas", locationFloor = "3", locationRoom = "Lab 302",
        status = TicketStatus.DIPROSES, reporterId = 1, reporterName = "Ahmad Faiz",
        agentId = 20, agentName = "Pak Budi", supportCount = 84, isSupportedByMe = true,
        createdAt = System.currentTimeMillis() - 2 * 3_600_000
    )
    TiketBantuTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TicketCard(base, {}, {}, commentCount = 12)
            TicketCard(base.copy(status = TicketStatus.BARU, agentName = null, isSupportedByMe = false, supportCount = 31), {}, {}, role = AppRole.AGEN, onClaimClick = {}, commentCount = 4)
        }
    }
}
