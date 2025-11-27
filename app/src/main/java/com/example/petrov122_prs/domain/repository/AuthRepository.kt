package com.example.petrov122_prs.domain.repository

import com.example.petrov122_prs.domain.models.AuthResult
import com.example.petrov122_prs.data.entities.UserEntity

interface AuthRepository {
    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ): AuthResult

    suspend fun login(
        identifier: String,
        password: String,
        isRememberMe: Boolean
    ): AuthResult

    suspend fun logout()

    suspend fun changePassword(
        userId: Long,
        currentPassword: String,
        newPassword: String
    ): AuthResult

    suspend fun isUserLoggedIn(): Boolean

    suspend fun getCurrentUser(): UserEntity?

    suspend fun updateUserProfile(
        userId: Long,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ): AuthResult

    suspend fun deleteAccount(userId: Long, password: String): AuthResult
}