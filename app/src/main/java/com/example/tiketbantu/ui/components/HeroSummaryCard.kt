package com.example.tiketbantu.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.ui.theme.BrandCyan
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.BrandIndigoSoft
import com.example.tiketbantu.ui.theme.BrandViolet
import com.example.tiketbantu.ui.theme.Hairline
import com.example.tiketbantu.ui.theme.Ink
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
    val cardShape = RoundedCornerShape(20.dp)
    Surface(
        onClick = onDetailClick,
        shape = cardShape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color(0xFFCBD5E1).copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = cardShape,
                ambientColor = BrandIndigo.copy(alpha = 0.08f),
                spotColor = BrandIndigo.copy(alpha = 0.12f)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFEEF2FF), // Very soft indigo tint
                        Color(0xFFE0F2FE), // Very soft cyan tint
                        Color(0xFFFAF5FF)  // Very soft violet tint
                    )
                ),
                shape = cardShape
            )
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(BrandIndigo.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Campaign, null, tint = BrandIndigo, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pantau Aduan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                        color = Ink
                    )
                    Text(
                        text = "Ringkasan statistik penanganan sarpras",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = InkMuted
                    )
                }
                TagChip(text = "REAL-TIME", container = BrandIndigo.copy(alpha = 0.1f), content = BrandIndigo)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Stat(openCount.toString(), "Aduan Terbuka", BrandIndigo, Modifier.weight(1f))
                Box(Modifier.width(1.dp).height(34.dp).background(Color(0xFFE2E8F0)))
                Stat(affectedCount.toString(), "Civitas Terdampak", SupportOrange, Modifier.weight(1f))
                Box(Modifier.width(1.dp).height(34.dp).background(Color(0xFFE2E8F0)))
                Stat(doneCount.toString(), "Berhasil Selesai", SuccessText, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Stat(value: String, label: String, color: Color, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 23.sp), color = color)
        Spacer(Modifier.height(2.dp))
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
