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
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft

/**
 * Modern Campus Glass Register Screen
 */
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.registerFormState.collectAsState()
    val uiState by viewModel.registerUiState.collectAsState()
    val isLoading = uiState is UiState.Loading

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            viewModel.resetRegisterUiState()
            onRegisterSuccess()
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
                    value = formState.name,
                    onValueChange = viewModel::onRegisterNameChanged,
                    placeholder = "Nama lengkap",
                    leadingIcon = { Icon(Icons.Outlined.Person, null, tint = InkMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    isError = formState.nameError != null
                )
                if (formState.nameError != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = formState.nameError ?: "",
                        color = DangerRed,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(Modifier.height(14.dp))

                FieldLabel(text = "Nomor Induk Mahasiswa (NIM / NIP)", required = true)
                Spacer(Modifier.height(6.dp))
                AppInput(
                    value = formState.nimNip,
                    onValueChange = viewModel::onRegisterNimNipChanged,
                    placeholder = "NIM atau NIP",
                    leadingIcon = { Icon(Icons.Outlined.Badge, null, tint = InkMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next),
                    isError = formState.nimNipError != null
                )
                if (formState.nimNipError != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = formState.nimNipError ?: "",
                        color = DangerRed,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(Modifier.height(14.dp))

                FieldLabel(text = "Email Kampus", required = true)
                Spacer(Modifier.height(6.dp))
                AppInput(
                    value = formState.email,
                    onValueChange = viewModel::onRegisterEmailChanged,
                    placeholder = "nama@unsoed.ac.id",
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

                Spacer(Modifier.height(14.dp))

                FieldLabel(text = "Kata Sandi", required = true)
                Spacer(Modifier.height(6.dp))
                AppInput(
                    value = formState.password,
                    onValueChange = viewModel::onRegisterPasswordChanged,
                    placeholder = "Minimal 6 karakter",
                    leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = InkMuted) },
                    trailingIcon = {
                        IconButton(onClick = viewModel::toggleRegisterPasswordVisibility) {
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

                if (uiState is UiState.Error) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = (uiState as UiState.Error).message,
                        color = DangerRed,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(Modifier.height(20.dp))

                GradientButton(
                    text = if (isLoading) "Mendaftarkan Akun..." else "Daftar Akun",
                    enabled = !isLoading && formState.name.isNotBlank() && formState.nimNip.isNotBlank() && formState.email.isNotBlank() && formState.password.isNotBlank(),
                    onClick = viewModel::register,
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
