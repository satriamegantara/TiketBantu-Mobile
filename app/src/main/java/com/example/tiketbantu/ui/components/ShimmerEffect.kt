package com.example.tiketbantu.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiketbantu.ui.theme.TiketBantuTheme

/**
 * Shimmer Loading Brush Extension & Skeleton Ticket Card Component.
 * Task 2.5 - Anggota 2 (UI/UX Specialist)
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslate"
    )

    val shimmerColors = listOf(
        Color(0xFFEDE9FE).copy(alpha = 0.4f),
        Color(0xFFDDD6FE).copy(alpha = 0.7f),
        Color(0xFFEDE9FE).copy(alpha = 0.4f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    background(brush = brush)
}

@Composable
fun TicketCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(16.dp)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Title skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(20.dp)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description skeleton lines
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(32.dp)
                    .shimmerEffect()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketCardSkeletonPreview() {
    TiketBantuTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TicketCardSkeleton()
        }
    }
}
