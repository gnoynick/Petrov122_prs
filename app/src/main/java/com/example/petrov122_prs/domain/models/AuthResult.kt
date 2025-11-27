package com.example.petrov122_prs.domain.models

sealed class AuthResult {
    data class Success(val userId: Long, val message: String? = null) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object RegistrationSuccess : AuthState()
    object LoginSuccess : AuthState()
    object LoggedOut : AuthState()
    object PasswordChanged : AuthState()
    data class Error(val message: String) : AuthState()
}

data class UserProfile(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val avatarUrl: String?,
    val lastLogin: String?,
    val createdAt: String
)