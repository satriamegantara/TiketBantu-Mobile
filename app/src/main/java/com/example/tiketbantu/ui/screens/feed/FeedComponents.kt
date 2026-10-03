package com.example.tiketbantu.ui.screens.feed

import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.ui.theme.StatusBadgeBaru
import com.example.tiketbantu.ui.theme.StatusBadgeDiproses
import com.example.tiketbantu.ui.theme.StatusBadgeDitutup
import com.example.tiketbantu.ui.theme.StatusBadgeSelesai
import com.example.tiketbantu.ui.theme.TiketBantuTheme

/*
 * TEMPORARY feed components (task 2.4 owner: Anggota 2).
 * TicketCard / StatusBadge / SupportButton live here only so the feed can run today.
 * When Anggota 2 publishes the shared versions in ui/components/, swap the imports in
 * FeedScreen.kt and delete this file. Parameter lists are kept minimal to ease the swap.
 */

@Composable
fun TicketCard(
    ticket: Ticket,
    onClick: () -> Unit,
    onToggleSupport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeAgo = remember(ticket.createdAt) {
        DateUtils.getRelativeTimeSpanString(
            ticket.createdAt,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }
    val location = remember(ticket) {
        listOf(ticket.locationBuilding, ticket.locationFloor, ticket.locationRoom)
            .filter { it.isNotBlank() }
            .joinToString(", ")
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(status = ticket.status)
                Spacer(Modifier.weight(1f))
                Text(
                    text = timeAgo,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = ticket.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = ticket.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            val meta = listOf(ticket.categoryName, location).filter { it.isNotBlank() }.joinToString(" | ")
            if (meta.isNotBlank()) {
                Text(
                    text = meta,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            SupportButton(
                supported = ticket.isSupportedByMe,
                count = ticket.supportCount,
                onClick = onToggleSupport
            )
        }
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (label, color) = when (status) {
        TicketStatus.BARU -> "Baru" to StatusBadgeBaru
        TicketStatus.DIPROSES -> "Diproses" to StatusBadgeDiproses
        TicketStatus.SELESAI -> "\u2713 Selesai" to StatusBadgeSelesai
        TicketStatus.DITUTUP -> "Ditutup" to StatusBadgeDitutup
        else -> status to MaterialTheme.colorScheme.outline
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.14f),
        contentColor = color
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

/** "Saya Juga Mengalami" toggle + counter. Filled when the current user already supports. */
@Composable
fun SupportButton(
    supported: Boolean,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonModifier = modifier
        .fillMaxWidth()
        .heightIn(min = 48.dp)
        .semantics { stateDescription = if (supported) "Sudah didukung" else "Belum didukung" }

    val content: @Composable RowScope.() -> Unit = {
        Icon(
            imageVector = if (supported) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text = "Saya Juga Mengalami", modifier = Modifier.weight(1f))
        Text(text = count.toString(), style = MaterialTheme.typography.labelLarge)
    }

    if (supported) {
        FilledTonalButton(onClick = onClick, modifier = buttonModifier, content = content)
    } else {
        OutlinedButton(onClick = onClick, modifier = buttonModifier, content = content)
    }
}

@Preview(showBackground = true)
@Composable
private fun TicketCardPreview() {
    TiketBantuTheme {
        TicketCard(
            ticket = Ticket(
                id = 1,
                title = "Wi-Fi gedung A sering putus",
                description = "Sinyal hilang tiap beberapa menit, terutama saat kuliah daring.",
                categoryId = 1,
                categoryName = "Teknologi & IT",
                locationBuilding = "Gedung A",
                locationFloor = "2",
                locationRoom = "201",
                reporterId = 10,
                supportCount = 5,
                isSupportedByMe = true
            ),
            onClick = {},
            onToggleSupport = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
