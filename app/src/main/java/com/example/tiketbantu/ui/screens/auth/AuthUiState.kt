package com.example.tiketbantu.ui.screens.auth

/**
 * Form input and validation state for the Login screen.
 */
data class LoginFormState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false
)

/**
 * Form input and validation state for the Register screen.
 */
data class RegisterFormState(
    val name: String = "",
    val nameError: String? = null,
    val nimNip: String = "",
    val nimNipError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false
)
