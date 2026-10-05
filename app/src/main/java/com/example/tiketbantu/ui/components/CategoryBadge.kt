package com.example.tiketbantu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CategoryStyle(
    val bg: Color,
    val text: Color,
    val border: Color,
    val icon: ImageVector
)

/**
 * Returns distinct colors and icons for each category:
 * - Jaringan: Soft Blue background, Deep Blue text/icon, Wifi icon
 * - Hardware: Soft Amber background, Warm Amber text/icon, Computer icon
 * - Software: Soft Violet background, Rich Purple text/icon, Code icon
 * - Fasilitas: Soft Emerald background, Deep Emerald text/icon, Apartment icon
 */
fun getCategoryStyle(categoryName: String?): CategoryStyle {
    val clean = categoryName?.trim()?.lowercase().orEmpty()
    return when {
        clean.contains("jaringan") || clean.contains("network") || clean.contains("wifi") -> CategoryStyle(
            bg = Color(0xFFEFF6FF),
            text = Color(0xFF1D4ED8),
            border = Color(0xFFBFDBFE),
            icon = Icons.Outlined.Wifi
        )
        clean.contains("hardware") || clean.contains("perangkat") || clean.contains("pc") -> CategoryStyle(
            bg = Color(0xFFFFFBEB),
            text = Color(0xFFB45309),
            border = Color(0xFFFDE68A),
            icon = Icons.Outlined.Computer
        )
        clean.contains("software") || clean.contains("aplikasi") || clean.contains("sistem") -> CategoryStyle(
            bg = Color(0xFFFAF5FF),
            text = Color(0xFF7E22CE),
            border = Color(0xFFE9D5FF),
            icon = Icons.Outlined.Code
        )
        clean.contains("fasilitas") || clean.contains("sarpras") || clean.contains("gedung") -> CategoryStyle(
            bg = Color(0xFFECFDF5),
            text = Color(0xFF047857),
            border = Color(0xFFA7F3D0),
            icon = Icons.Outlined.Apartment
        )
        else -> CategoryStyle(
            bg = Color(0xFFF1F5F9),
            text = Color(0xFF475569),
            border = Color(0xFFE2E8F0),
            icon = Icons.Outlined.Category
        )
    }
}

/**
 * Modern pill badge displaying ticket category with distinct colors per category.
 */
@Composable
fun CategoryBadge(
    categoryName: String,
    modifier: Modifier = Modifier
) {
    val style = getCategoryStyle(categoryName)
    val displayLabel = categoryName.ifBlank { "Umum" }

    Surface(
        shape = RoundedCornerShape(50),
        color = style.bg,
        border = BorderStroke(1.dp, style.border),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = style.icon,
                contentDescription = null,
                tint = style.text,
                modifier = Modifier.size(13.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = displayLabel,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                ),
                color = style.text,
                maxLines = 1
            )
        }
    }
}
