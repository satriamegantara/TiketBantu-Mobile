package com.example.tiketbantu.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.ui.theme.StatusBadgeBaru
import com.example.tiketbantu.ui.theme.StatusBadgeDiproses
import com.example.tiketbantu.ui.theme.StatusBadgeDitutup
import com.example.tiketbantu.ui.theme.StatusBadgeSelesai
import com.example.tiketbantu.ui.theme.TiketBantuTheme

/**
 * Material 3 Status Badge component with distinctive colors and checkmark indicator for finished tickets.
 * Task 2.4 - Anggota 2 (UI/UX Specialist)
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (status) {
        TicketStatus.BARU -> Triple(
            StatusBadgeBaru.copy(alpha = 0.15f),
            StatusBadgeBaru,
            "BARU"
        )
        TicketStatus.DIPROSES -> Triple(
            StatusBadgeDiproses.copy(alpha = 0.18f),
            Color(0xFFB45309), // Darker amber for contrast
            "DIPROSES"
        )
        TicketStatus.SELESAI -> Triple(
            StatusBadgeSelesai.copy(alpha = 0.18f),
            Color(0xFF047857), // Darker green for WCAG AA
            "✓ SELESAI"
        )
        TicketStatus.DITUTUP -> Triple(
            StatusBadgeDitutup.copy(alpha = 0.15f),
            StatusBadgeDitutup,
            "DITUTUP"
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            status.uppercase()
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                ),
                color = textColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatusBadgePreview() {
    TiketBantuTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            StatusBadge(status = TicketStatus.BARU)
            StatusBadge(status = TicketStatus.DIPROSES)
            StatusBadge(status = TicketStatus.SELESAI)
            StatusBadge(status = TicketStatus.DITUTUP)
        }
    }
}
