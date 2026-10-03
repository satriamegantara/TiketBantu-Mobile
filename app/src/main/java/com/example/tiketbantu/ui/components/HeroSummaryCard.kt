package com.example.tiketbantu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.Hairline
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import com.example.tiketbantu.ui.theme.SuccessText
import com.example.tiketbantu.ui.theme.SupportOrange
import com.example.tiketbantu.ui.theme.TiketBantuTheme

/**
 * "Transparansi Kampus" summary (feature 2.7): open tickets, affected civitas, finished.
 */
@Composable
fun HeroSummaryCard(
    openCount: Int,
    affectedCount: Int,
    doneCount: Int,
    modifier: Modifier = Modifier,
    onDetailClick: () -> Unit = {}
) {
    GlassCard(modifier = modifier.fillMaxWidth(), onClick = onDetailClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Campaign, null, tint = BrandIndigo, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "Transparansi Kampus",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Text(
                "Detail Real-time",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = BrandIndigo
            )
        }
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Stat(openCount.toString(), "Aduan Terbuka", BrandIndigo, Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(36.dp).background(Hairline))
            Stat(affectedCount.toString(), "Civitas Terdampak", SupportOrange, Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(36.dp).background(Hairline))
            Stat(doneCount.toString(), "Selesai Pekan Ini", SuccessText, Modifier.weight(1f))
        }
    }
}

@Composable
private fun Stat(value: String, label: String, color: Color, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp), color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = InkMuted, textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F6FB)
@Composable
private fun HeroSummaryPreview() {
    TiketBantuTheme {
        Column(Modifier.padding(16.dp)) {
            HeroSummaryCard(openCount = 42, affectedCount = 128, doneCount = 18)
            Text("", color = InkSoft)
        }
    }
}
