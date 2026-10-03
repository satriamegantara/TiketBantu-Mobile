package com.example.tiketbantu.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.ui.theme.Primary
import com.example.tiketbantu.ui.theme.PrimaryContainer
import com.example.tiketbantu.ui.theme.TiketBantuTheme

/**
 * "Saya Juga Mengalami" (Most Liked) support button component with M3 micro-animations.
 * Task 2.4 - Anggota 2 (UI/UX Specialist)
 */
@Composable
fun MostLikedButton(
    isSupported: Boolean,
    likeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSupported) PrimaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        label = "MostLikedBg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSupported) Primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "MostLikedContentColor"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (isSupported) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "MostLikedIconScale"
    )

    Surface(
        onClick = onClick,
        enabled = enabled,
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSupported) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                contentDescription = "Dukung aduan ini (Saya juga mengalami)",
                tint = contentColor,
                modifier = Modifier
                    .size(16.dp)
                    .scale(iconScale)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Dukung ($likeCount)",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSupported) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = contentColor
            )
        }
    }
}

/** Alias for SupportButton compatibility across Detail & Feed screens */
@Composable
fun SupportButton(
    supported: Boolean,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MostLikedButton(
        isSupported = supported,
        likeCount = count,
        onClick = onClick,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MostLikedButtonPreview() {
    TiketBantuTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            MostLikedButton(
                isSupported = false,
                likeCount = 14,
                onClick = {}
            )
            MostLikedButton(
                isSupported = true,
                likeCount = 15,
                onClick = {}
            )
        }
    }
}
