package com.example.tiketbantu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.R
import com.example.tiketbantu.ui.session.AppRole
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import com.example.tiketbantu.ui.theme.TiketBantuTheme

/** TiketBantu brand emblem loaded from res/drawable/emblem.png */
@Composable
fun BrandEmblem(
    modifier: Modifier = Modifier,
    tint: Color? = null
) {
    Image(
        painter = painterResource(id = R.drawable.emblem),
        contentDescription = "Logo TiketBantu",
        modifier = Modifier
            .size(36.dp)
            .then(modifier),
        contentScale = ContentScale.Fit,
        colorFilter = tint?.let { ColorFilter.tint(it) }
    )
}

/**
 * Home header: emblem, avatar, and modern rich greeting headline.
 */
@Composable
fun UserGreetingHeader(
    userName: String,
    modifier: Modifier = Modifier,
    role: AppRole = AppRole.PELAPOR,
    onAvatarClick: () -> Unit = {}
) {
    val firstName = userName.trim().split(" ")
        .firstOrNull { it.isNotBlank() && !it.endsWith(".") }
        ?.ifBlank { null } ?: "Pengguna"

    val (greetingTitle, greetingSubtitle) = when (role) {
        AppRole.AGEN -> "Siap Menangani Tugas Hari Ini?" to "Pantau aduan yang ditugaskan dan perbarui progres pengerjaannya."
        AppRole.ADMIN -> "Ringkasan Operasional" to "Pantau dan kelola layanan sarana prasarana kampus."
        else -> "Halo, $firstName" to "Ada kendala fasilitas yang perlu dibantu hari ini?"
    }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                BrandEmblem(modifier = Modifier.size(40.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Tiket",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Ink
                        )
                        Text(
                            text = "Bantu",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = BrandIndigo
                        )
                    }
                    Text(
                        text = "Layanan Sarpras Kampus",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = InkMuted
                    )
                }
            }

            Surface(
                onClick = onAvatarClick,
                shape = CircleShape,
                color = Color.Transparent
            ) {
                InitialsAvatar(name = userName, size = 40.dp, soft = false)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = greetingTitle,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 25.sp,
                letterSpacing = (-0.4).sp
            ),
            color = Ink
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = greetingSubtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            color = InkSoft
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HeaderPreview() {
    TiketBantuTheme {
        Column {
            UserGreetingHeader(userName = "Emily Johnson")
        }
    }
}
