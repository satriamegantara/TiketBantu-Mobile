package com.example.tiketbantu.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft

/**
 * Modern Campus Glass Register Screen
 */
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                text = "Daftar Akun Baru",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
                color = Ink
            )
            Text(
                text = "Bergabung bersama ribuan mahasiswa dalam transparansi kampus",
                style = MaterialTheme.typography.bodyMedium,
                color = InkMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                FieldLabel(text = "Nama Lengkap", required = true)
                Spacer(Modifier.height(6.dp))
                AppInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Misal: Budi Pratama",
                    leadingIcon = { Icon(Icons.Outlined.Person, null, tint = InkMuted) }
                )

                Spacer(Modifier.height(14.dp))

                FieldLabel(text = "Email Kampus", required = true)
                Spacer(Modifier.height(6.dp))
                AppInput(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "nim@mhs.kampus.ac.id",
                    leadingIcon = { Icon(Icons.Outlined.Email, null, tint = InkMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(Modifier.height(14.dp))

                FieldLabel(text = "Nomor Induk Mahasiswa (NIM)", required = true)
                Spacer(Modifier.height(6.dp))
                AppInput(
                    value = nim,
                    onValueChange = { nim = it },
                    placeholder = "Misal: 20210801001",
                    leadingIcon = { Icon(Icons.Outlined.Badge, null, tint = InkMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(14.dp))

                FieldLabel(text = "Kata Sandi", required = true)
                Spacer(Modifier.height(6.dp))
                AppInput(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Minimal 8 karakter",
                    leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = InkMuted) },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(Modifier.height(24.dp))

                GradientButton(
                    text = "Daftar Akun",
                    onClick = {
                        DemoSession.loginAs(AppRole.PELAPOR)
                        if (name.isNotBlank()) DemoSession.name = name
                        if (email.isNotBlank()) DemoSession.email = email
                        if (nim.isNotBlank()) DemoSession.nimNip = nim
                        onRegisterSuccess()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sudah punya akun?", style = MaterialTheme.typography.bodyMedium, color = InkSoft)
                TextButton(onClick = onNavigateToLogin) {
                    Text("Masuk Sekarang", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = BrandIndigo)
                }
            }
        }
    }
}
