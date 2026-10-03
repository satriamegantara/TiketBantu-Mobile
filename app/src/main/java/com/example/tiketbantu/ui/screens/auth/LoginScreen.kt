package com.example.tiketbantu.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.AppInput
import com.example.tiketbantu.ui.components.BrandEmblem
import com.example.tiketbantu.ui.components.FieldLabel
import com.example.tiketbantu.ui.components.GlassCard
import com.example.tiketbantu.ui.components.GradientButton
import com.example.tiketbantu.ui.session.AppRole
import com.example.tiketbantu.ui.session.DemoSession
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.FieldBg
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft

/**
 * Modern Glass Login Screen with Instant Demo Role login selectors
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("emily.j@kampus.ac.id") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }

    AppBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BrandEmblem(tint = BrandIndigo, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                text = "TiketBantu Mobile",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
                color = Ink
            )
            Text(
                text = "Sistem Transparansi Fasilitas & Sarpras Kampus",
                style = MaterialTheme.typography.bodyMedium,
                color = InkMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                FieldLabel(text = "Email Kampus", required = true)
                Spacer(Modifier.height(6.dp))
                AppInput(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "nama@kampus.ac.id",
                    leadingIcon = { Icon(Icons.Outlined.Email, null, tint = InkMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(Modifier.height(16.dp))

                FieldLabel(text = "Kata Sandi", required = true)
                Spacer(Modifier.height(6.dp))
                AppInput(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "••••••••",
                    leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = InkMuted) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = null,
                                tint = InkMuted
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                )

                Spacer(Modifier.height(24.dp))

                GradientButton(
                    text = "Masuk ke Akun",
                    onClick = {
                        DemoSession.isLoggedIn = true
                        onLoginSuccess()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            // Demo Login Shortcut Box
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Akses Cepat Demo (Multi-Role):",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Ink
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DemoRoleButton("Pelapor", "Mahasiswa", modifier = Modifier.weight(1f)) {
                        DemoSession.loginAs(AppRole.PELAPOR)
                        onLoginSuccess()
                    }
                    DemoRoleButton("Teknisi", "Pak Joko", modifier = Modifier.weight(1f)) {
                        DemoSession.loginAs(AppRole.AGEN)
                        onLoginSuccess()
                    }
                    DemoRoleButton("Admin", "Sarpras", modifier = Modifier.weight(1f)) {
                        DemoSession.loginAs(AppRole.ADMIN)
                        onLoginSuccess()
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Belum memiliki akun?", style = MaterialTheme.typography.bodyMedium, color = InkSoft)
                TextButton(onClick = onNavigateToRegister) {
                    Text("Daftar Sekarang", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = BrandIndigo)
                }
            }
        }
    }
}

@Composable
private fun DemoRoleButton(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = FieldBg,
        modifier = modifier.height(52.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = BrandIndigo)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = InkMuted)
        }
    }
}
