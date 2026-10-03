package com.example.tiketbantu.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import com.example.tiketbantu.ui.theme.TiketBantuTheme

/** TiketBantu emblem: three connected dots (propeller mark from the reference). */
@Composable
fun BrandEmblem(modifier: Modifier = Modifier, tint: Color = Ink) {
    Box(modifier = modifier.size(36.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(26.dp)) {
            val r = size.minDimension * 0.22f
            val c = Offset(size.width / 2, size.height / 2)
            drawCircle(tint, r, Offset(c.x, r))
            drawCircle(tint, r, Offset(r, size.height - r))
            drawCircle(tint, r, Offset(size.width - r, size.height - r))
            drawCircle(tint, r * 0.75f, Offset(c.x, c.y + r * 0.35f))
        }
    }
}

/**
 * Home header: emblem, notification bell + avatar, and the friendly two-line greeting
 * ("Hi Emily, ada yang bisa / kami bantu hari ini?"). Feature 1.4 (profile bar).
 */
@Composable
fun UserGreetingHeader(
    userName: String,
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrandEmblem()
            Spacer(Modifier.weight(1f))
            CircleIconButton(
                icon = Icons.Outlined.Notifications,
                contentDescription = "Notifikasi",
                onClick = onNotificationClick,
                showBadge = true
            )
            Spacer(Modifier.width(10.dp))
            androidx.compose.material3.Surface(
                onClick = onAvatarClick,
                shape = androidx.compose.foundation.shape.CircleShape,
                color = Color.Transparent
            ) {
                InitialsAvatar(name = userName, size = 40.dp, soft = false)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = buildGreeting(userName.substringBefore(' ')),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
            color = InkSoft
        )
        Text(
            text = "kami bantu hari ini?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 26.sp),
            color = Ink
        )
    }
}

private fun buildGreeting(first: String) = "Hi $first, ada yang bisa"

/**
 * Sub-page header: circular back button, title + optional subtitle, trailing actions.
 * Matches the Detail / Create screens of the reference.
 */
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            CircleIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                onClick = onBackClick
            )
            Spacer(Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = InkMuted, maxLines = 1)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically, content = actions)
    }
}

@Preview(showBackground = true)
@Composable
private fun HeaderPreview() {
    TiketBantuTheme {
        Column {
            UserGreetingHeader(userName = "Emily Johnson")
            AppTopBar(title = "Aduan Saya", subtitle = "7 laporan", onBackClick = {})
        }
    }
}
