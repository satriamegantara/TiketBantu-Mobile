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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.base.UiState
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.AppInput
import com.example.tiketbantu.ui.components.BrandEmblem
import com.example.tiketbantu.ui.components.FieldLabel
import com.example.tiketbantu.ui.components.GlassCard
import com.example.tiketbantu.ui.components.GradientButton
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.DangerRed
import com.example.tiketbantu.ui.theme.FieldBg
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft

/**
 * Modern Glass Login Screen with Instant Demo Role login selectors and ViewModel binding.
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.loginFormState.collectAsState()
    val loginUiState by viewModel.loginUiState.collectAsState()
    val isLoading = loginUiState is UiState.Loading

    LaunchedEffect(loginUiState) {
        if (loginUiState is UiState.Success) {
            onLoginSuccess()
            viewModel.resetLoginUiState()
        }
    }

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
                    value = formState.email,
                    onValueChange = viewModel::onLoginEmailChanged,
                    placeholder = "nama@kampus.ac.id",
                    leadingIcon = { Icon(Icons.Outlined.Email, null, tint = InkMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    isError = formState.emailError != null
                )
                if (formState.emailError != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = formState.emailError ?: "",
                        color = DangerRed,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(Modifier.height(16.dp))

                FieldLabel(text = "Kata Sandi", required = true)
                Spacer(Modifier.height(6.dp))
                AppInput(
                    value = formState.password,
                    onValueChange = viewModel::onLoginPasswordChanged,
                    placeholder = "••••••••",
                    leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = InkMuted) },
                    trailingIcon = {
                        IconButton(onClick = viewModel::toggleLoginPasswordVisibility) {
                            Icon(
                                if (formState.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = null,
                                tint = InkMuted
                            )
                        }
                    },
                    visualTransformation = if (formState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    isError = formState.passwordError != null
                )
                if (formState.passwordError != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = formState.passwordError ?: "",
                        color = DangerRed,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                if (loginUiState is UiState.Error) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = (loginUiState as UiState.Error).message,
                        color = DangerRed,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(Modifier.height(20.dp))

                GradientButton(
                    text = if (isLoading) "Memproses..." else "Masuk ke Akun",
                    enabled = !isLoading && formState.email.isNotBlank(),
                    onClick = viewModel::login,
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
                    DemoRoleButton("Pelapor", "Emily", modifier = Modifier.weight(1f)) {
                        viewModel.fillDemoAccount("PELAPOR")
                        viewModel.login()
                    }
                    DemoRoleButton("Teknisi", "Pak Joko", modifier = Modifier.weight(1f)) {
                        viewModel.fillDemoAccount("AGEN")
                        viewModel.login()
                    }
                    DemoRoleButton("Admin", "Sarpras", modifier = Modifier.weight(1f)) {
                        viewModel.fillDemoAccount("ADMIN")
                        viewModel.login()
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
