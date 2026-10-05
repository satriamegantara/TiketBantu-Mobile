package com.example.tiketbantu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.ui.session.AppRole
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

private fun formatTicketId(id: Long): String = "#TKT-${id.toString().padStart(4, '0')}"

/**
 * Custom Ticket Stub Shape featuring semi-circular side cutouts (notches)
 * separating the upper body content from the lower action footer.
 */
class TicketStubShape(
    private val cornerRadius: Dp = 20.dp,
    private val notchRadius: Dp = 10.dp,
    private val footerHeight: Dp = 64.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cr = with(density) { cornerRadius.toPx() }
        val nr = with(density) { notchRadius.toPx() }
        val notchY = size.height - with(density) { footerHeight.toPx() }

        val path = Path().apply {
            reset()
            // Top-left corner
            moveTo(cr, 0f)
            lineTo(size.width - cr, 0f)
            arcTo(
                rect = Rect(size.width - 2 * cr, 0f, size.width, 2 * cr),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Right edge down to notch
            lineTo(size.width, notchY - nr)
            // Right inward cutout notch
            arcTo(
                rect = Rect(size.width - nr, notchY - nr, size.width + nr, notchY + nr),
                startAngleDegrees = -90f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            // Right edge down to bottom-right
            lineTo(size.width, size.height - cr)
            arcTo(
                rect = Rect(size.width - 2 * cr, size.height - 2 * cr, size.width, size.height),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Bottom edge
            lineTo(cr, size.height)
            arcTo(
                rect = Rect(0f, size.height - 2 * cr, 2 * cr, size.height),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Left edge up to notch
            lineTo(0f, notchY + nr)
            // Left inward cutout notch
            arcTo(
                rect = Rect(-nr, notchY - nr, nr, notchY + nr),
                startAngleDegrees = 90f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            // Left edge up to top-left corner
            lineTo(0f, cr)
            arcTo(
                rect = Rect(0f, 0f, 2 * cr, 2 * cr),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Ticket Card Component designed with authentic ticket stub cutouts & dotted tear line:
 * - ID Tiket: Plain neutral text (tanpa background warna)
 * - Header Row: ID + Relative Time + Status Pill (Sedang Dikerjakan)
 * - Title & Location Pill
 * - Description & Photo Bukti Badge Overlay
 * - Technician Handler Box
 * - Dotted Tear-Line Divider with side cutouts
 * - Footer: Detail Aduan + Share + Blue Dukung button
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

    Surface(
        onClick = onClick,
        shape = TicketStubShape(cornerRadius = 20.dp, notchRadius = 10.dp, footerHeight = 64.dp),
        color = if (finished) Color.White.copy(alpha = 0.95f) else Color.White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Main Card Content Body
            Column(
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 14.dp)
            ) {
                // 1. Top Metadata Row: Uncolored Ticket ID + Relative Time + Status Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatTicketId(ticket.id),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = Color(0xFF64748B)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = relativeTime(ticket.createdAt),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = InkMuted
                    )
                    Spacer(Modifier.weight(1f))
                    CustomStatusPill(ticket.status)
                }

                Spacer(Modifier.height(12.dp))

                // 2. Title
                Text(
                    text = ticket.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 19.sp,
                        lineHeight = 25.sp
                    ),
                    color = Ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(10.dp))

                // 3. Category & Location Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val resolvedCategory = ticket.categoryName.ifBlank {
                        when (ticket.categoryId) {
                            1L -> "Jaringan"
                            2L -> "Hardware"
                            3L -> "Software"
                            4L -> "Fasilitas"
                            else -> "Umum"
                        }
                    }
                    CategoryBadge(categoryName = resolvedCategory)

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFE0F7FA),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Place,
                                contentDescription = null,
                                tint = Color(0xFF00838F),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = ticket.locationText().ifBlank { "Lokasi Fasilitas Kampus" },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = Color(0xFF00838F),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // 4. Description
                Text(
                    text = ticket.description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = InkSoft,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                // 5. Photo Attachment (if present)
                ticket.imageUrl?.takeIf { it.isNotBlank() }?.let { path ->
                    Spacer(Modifier.height(14.dp))
                    Box {
                        AsyncImage(
                            model = if (path.startsWith("content:") || path.startsWith("file:")) path else File(path),
                            contentDescription = "Foto bukti",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(FieldBg)
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.92f),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PhotoCamera,
                                    contentDescription = null,
                                    tint = Color(0xFF00838F),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Foto Bukti Lapangan",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Ink
                                )
                            }
                        }
                    }
                }

                // 6. Technician Handler / Claim Box
                Spacer(Modifier.height(14.dp))
                HandlerBox(ticket = ticket, role = role, onClaimClick = onClaimClick)
            }

            // 7. Dotted Tear-Line Divider connecting the side cutouts
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 14.dp)
            ) {
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                )
            }

            // 8. Footer Action Bar Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Detail Aduan Button
                Surface(
                    onClick = onClick,
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = if (role == AppRole.AGEN) "Detail Tugas" else "Detail Aduan",
                            tint = Color(0xFF00838F),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (role == AppRole.AGEN) "Detail Tugas" else "Detail Aduan",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Ink
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CommentPill(
                        count = commentCount,
                        onClick = onClick
                    )

                    if (role == AppRole.AGEN || role == AppRole.ADMIN) {
                        InfoCountPill(
                            icon = Icons.Outlined.FavoriteBorder,
                            count = ticket.supportCount,
                            contentDescription = "Jumlah dukungan"
                        )
                    } else if (ticket.status == TicketStatus.SELESAI) {
                        TagChip(
                            text = "${ticket.supportCount} Terbantu",
                            container = SuccessSoftBg,
                            content = SuccessText
                        )
                    } else {
                        SupportPill(
                            count = ticket.supportCount,
                            supported = ticket.isSupportedByMe,
                            onClick = onToggleSupport,
                            enabled = !finished
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomStatusPill(status: String) {
    val (bg, fg, label) = when (status) {
        TicketStatus.DIPROSES -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), "Sedang Dikerjakan")
        TicketStatus.BARU -> Triple(Color(0xFFE0F2FE), Color(0xFF0284C7), "Baru")
        TicketStatus.SELESAI -> Triple(SuccessSoftBg, SuccessText, "Selesai")
        else -> Triple(Color(0xFFF1F5F9), InkMuted, "Ditutup")
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = bg,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = fg,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
            )
        }
    }
}

@Composable
private fun BlueSupportPill(
    count: Int,
    supported: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        color = if (supported) Color(0xFF1D4ED8) else Color(0xFF2563EB),
        modifier = modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Dukung",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Dukung",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp)
            )
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFF06B6D4)
            ) {
                Text(
                    text = "$count",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun HandlerBox(ticket: Ticket, role: AppRole, onClaimClick: (() -> Unit)?) {
    val shape = RoundedCornerShape(14.dp)
    val isStaff = role == AppRole.AGEN || role == AppRole.ADMIN

    when {
        ticket.status == TicketStatus.SELESAI -> {
            Surface(shape = shape, color = SuccessSoftBg, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Aduan Telah Dituntaskan", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = SuccessText)
                    Text(
                        if (isStaff && ticket.agentName != null) "Ditangani oleh ${ticket.agentName}. Tiket terkunci dari pengeditan." else "Tiket terkunci dari pengeditan.",
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
        isStaff && (ticket.status == TicketStatus.DIPROSES || ticket.agentName != null) -> {
            val agent = ticket.agentName ?: "Agen Sarpras"
            Surface(
                shape = shape,
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InitialsAvatar(agent, size = 38.dp, soft = false)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(agent, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Ink)
                        Text("Teknisi Sarpras & Kelistrikan", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFF00838F))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Di Lokasi", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF00838F))
                        }
                    }
                }
            }
        }
        isStaff -> {
            Surface(
                shape = shape,
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null,
                            tint = Color(0xFF1E40AF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Belum ada teknisi ditugaskan",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF1E40AF),
                        modifier = Modifier.weight(1f)
                    )
                    if (role == AppRole.AGEN && onClaimClick != null) {
                        Surface(
                            onClick = onClaimClick,
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF003399)
                        ) {
                            Text(
                                text = "Klaim Tiket",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                            )
                        }
                    }
                }
            }
        }
        else -> {
            // For Pelapor: If assigned, show technician badge
            if (ticket.agentName != null) {
                Surface(
                    shape = shape,
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InitialsAvatar(ticket.agentName, size = 38.dp, soft = false)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(ticket.agentName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Ink)
                            Text("Teknisi Sarpras & Kelistrikan", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFF00838F))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Di Lokasi", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF00838F))
                            }
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
        id = 842, title = "AC Lab Komputer 302 Mati Total & Bocor Air",
        description = "Hawa ruangan sangat panas dan air rembesan menetes ke meja server nomor 4. Dikhawatirkan korsleting saat sesi praktikum Algoritma siang ini.",
        categoryId = 2, categoryName = "Fasilitas Ruangan",
        locationBuilding = "Gd. Thomas Aquinas", locationFloor = "3", locationRoom = "Lab 302",
        status = TicketStatus.DIPROSES, reporterId = 1, reporterName = "Ahmad Faiz",
        agentId = 20, agentName = "Pak Budi Santoso", supportCount = 84, isSupportedByMe = true,
        createdAt = System.currentTimeMillis() - 12 * 60_000
    )
    TiketBantuTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TicketCard(base, {}, {}, commentCount = 12)
            TicketCard(base.copy(id = 201, title = "Proyektor Ruang Seminar R.201 Kedip-kedip", status = TicketStatus.BARU, agentName = null, isSupportedByMe = false, supportCount = 31), {}, {}, role = AppRole.AGEN, onClaimClick = {}, commentCount = 4)
        }
    }
}
