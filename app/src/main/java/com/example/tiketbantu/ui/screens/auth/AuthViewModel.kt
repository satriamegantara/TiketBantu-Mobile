package com.example.tiketbantu.ui.screens.auth

import com.example.tiketbantu.base.BaseViewModel
import com.example.tiketbantu.base.UiState
import com.example.tiketbantu.domain.model.User
import com.example.tiketbantu.domain.repository.AuthRepository
import java.security.MessageDigest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing authentication state, form validation, and SHA-256 password hashing.
 *
 * Exposes form input states ([LoginFormState], [RegisterFormState]) and operation states
 * as [UiState] ([UiState.Idle], [UiState.Loading], [UiState.Success], [UiState.Error]).
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    // ── Form States ───────────────────────────────────────────────────────────
    private val _loginFormState = MutableStateFlow(LoginFormState())
    val loginFormState: StateFlow<LoginFormState> = _loginFormState.asStateFlow()

    private val _registerFormState = MutableStateFlow(RegisterFormState())
    val registerFormState: StateFlow<RegisterFormState> = _registerFormState.asStateFlow()

    // ── Operation Result States (bawaan com.example.tiketbantu.base.UiState) ──
    private val _loginUiState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val loginUiState: StateFlow<UiState<User>> = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val registerUiState: StateFlow<UiState<User>> = _registerUiState.asStateFlow()

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        private val ALPHANUMERIC_NIM_NIP_REGEX = "^[a-zA-Z0-9]{5,20}$".toRegex()
    }

    /**
     * Hashes the given [input] string using SHA-256 and returns it as a lowercase hex string.
     */
    private fun hashSha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // ── Login Form Handlers ───────────────────────────────────────────────────

    fun onLoginEmailChanged(email: String) {
        _loginFormState.update { it.copy(email = email, emailError = null) }
        if (_loginUiState.value is UiState.Error) {
            _loginUiState.value = UiState.Idle
        }
    }

    fun onLoginPasswordChanged(password: String) {
        _loginFormState.update { it.copy(password = password, passwordError = null) }
        if (_loginUiState.value is UiState.Error) {
            _loginUiState.value = UiState.Idle
        }
    }

    fun toggleLoginPasswordVisibility() {
        _loginFormState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        val currentForm = _loginFormState.value
        val email = currentForm.email.trim()
        val password = currentForm.password

        var hasError = false
        var emailError: String? = null
        var passwordError: String? = null

        if (email.isBlank()) {
            emailError = "Email wajib diisi"
            hasError = true
        } else if (email.contains("@") && !EMAIL_REGEX.matches(email)) {
            emailError = "Format email tidak valid"
            hasError = true
        }

        if (password.isBlank()) {
            passwordError = "Kata sandi wajib diisi"
            hasError = true
        }

        if (hasError) {
            _loginFormState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        _loginUiState.value = UiState.Loading

        launchSafe {
            val passwordHash = hashSha256(password)
            val result = authRepository.login(email = email, passwordHash = passwordHash)
            result.onSuccess { user ->
                _loginUiState.value = UiState.Success(user)
            }.onFailure { throwable ->
                _loginUiState.value = UiState.Error(
                    message = throwable.message ?: "Terjadi kesalahan saat masuk",
                    throwable = throwable
                )
            }
        }
    }

    fun resetLoginUiState() {
        _loginUiState.value = UiState.Idle
    }

    // ── Register Form Handlers ────────────────────────────────────────────────

    fun onRegisterNameChanged(name: String) {
        _registerFormState.update { it.copy(name = name, nameError = null) }
        if (_registerUiState.value is UiState.Error) {
            _registerUiState.value = UiState.Idle
        }
    }

    fun onRegisterNimNipChanged(nimNip: String) {
        _registerFormState.update { it.copy(nimNip = nimNip, nimNipError = null) }
        if (_registerUiState.value is UiState.Error) {
            _registerUiState.value = UiState.Idle
        }
    }

    fun onRegisterEmailChanged(email: String) {
        _registerFormState.update { it.copy(email = email, emailError = null) }
        if (_registerUiState.value is UiState.Error) {
            _registerUiState.value = UiState.Idle
        }
    }

    fun onRegisterPasswordChanged(password: String) {
        _registerFormState.update { it.copy(password = password, passwordError = null) }
        if (_registerUiState.value is UiState.Error) {
            _registerUiState.value = UiState.Idle
        }
    }

    fun toggleRegisterPasswordVisibility() {
        _registerFormState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun register() {
        val currentForm = _registerFormState.value
        val name = currentForm.name.trim()
        val nimNip = currentForm.nimNip.trim()
        val email = currentForm.email.trim()
        val password = currentForm.password

        var hasError = false
        var nameError: String? = null
        var nimNipError: String? = null
        var emailError: String? = null
        var passwordError: String? = null

        // 1. Nama Lengkap: Wajib, minimal 3 karakter
        if (name.isBlank()) {
            nameError = "Nama lengkap wajib diisi"
            hasError = true
        } else if (name.length < 3) {
            nameError = "Nama minimal 3 karakter"
            hasError = true
        }

        // 2. NIM/NIP: Opsional, jika diisi hanya huruf dan angka, 5 sampai 20 karakter
        if (nimNip.isNotBlank()) {
            if (!ALPHANUMERIC_NIM_NIP_REGEX.matches(nimNip)) {
                nimNipError = "NIM/NIP harus berupa huruf atau angka (5 sampai 20 karakter)"
                hasError = true
            }
        }

        // 3. Email: Wajib, format email valid
        if (email.isBlank()) {
            emailError = "Email wajib diisi"
            hasError = true
        } else if (!EMAIL_REGEX.matches(email)) {
            emailError = "Format email tidak valid"
            hasError = true
        }

        // 4. Kata Sandi: Wajib, minimal 6 karakter
        if (password.isBlank()) {
            passwordError = "Kata sandi wajib diisi"
            hasError = true
        } else if (password.length < 6) {
            passwordError = "Kata sandi minimal 6 karakter"
            hasError = true
        }

        if (hasError) {
            _registerFormState.update {
                it.copy(
                    nameError = nameError,
                    nimNipError = nimNipError,
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        _registerUiState.value = UiState.Loading

        launchSafe {
            val passwordHash = hashSha256(password)
            val result = authRepository.register(
                name = name,
                email = email,
                nimNip = nimNip.ifBlank { null },
                passwordHash = passwordHash
            )
            result.onSuccess { user ->
                _registerUiState.value = UiState.Success(user)
            }.onFailure { throwable ->
                _registerUiState.value = UiState.Error(
                    message = throwable.message ?: "Terjadi kesalahan saat registrasi",
                    throwable = throwable
                )
            }
        }
    }

    fun resetRegisterUiState() {
        _registerUiState.value = UiState.Idle
    }
}
