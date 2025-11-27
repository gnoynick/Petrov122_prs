package com.example.petrov122_prs.data.repository

import com.example.petrov122_prs.data.dao.SessionDao
import com.example.petrov122_prs.data.dao.UserDao
import com.example.petrov122_prs.data.entities.UserEntity
import com.example.petrov122_prs.domain.models.AuthResult
import com.example.petrov122_prs.domain.repository.AuthRepository
import com.example.petrov122_prs.domain.utils.PasswordManager
import com.example.petrov122_prs.domain.utils.PasswordStrength
import com.example.petrov122_prs.domain.utils.PreferencesManager
import com.example.petrov122_prs.domain.utils.SessionManager
import kotlinx.coroutines.flow.first
import java.util.Date

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val sessionDao: SessionDao,
    private val passwordManager: PasswordManager,
    private val sessionManager: SessionManager,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ): AuthResult {
        return try {
            if (userDao.checkEmailExists(email) > 0) {
                return AuthResult.Error("Email уже зарегистрирован")
            }

            if (userDao.checkUsernameExists(username) > 0) {
                return AuthResult.Error("Имя пользователя уже занято")
            }

            val passwordStrength = passwordManager.isPasswordStrong(password)
            if (passwordStrength == PasswordStrength.WEAK) {
                return AuthResult.Error("Пароль слишком слабый")
            }

            val salt = passwordManager.generateSalt()
            val passwordHash = passwordManager.hashPassword(password, salt)

            val user = UserEntity(
                username = username,
                email = email,
                passwordHash = passwordHash,
                salt = salt,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                avatarUrl = null,
                lastLogin = null,
                createdAt = Date(),
                updatedAt = Date()
            )

            val userId = userDao.insertUser(user)
            AuthResult.Success(userId)

        } catch (e: Exception) {
            AuthResult.Error("Ошибка регистрации: ${e.message}")
        }
    }

    override suspend fun login(
        identifier: String,
        password: String,
        isRememberMe: Boolean
    ): AuthResult {
        return try {
            val user = userDao.getUserByEmail(identifier)
                ?: userDao.getUserByUsername(identifier)
                ?: return AuthResult.Error("Пользователь не найден")

            if (!passwordManager.verifyPassword(password, user.salt, user.passwordHash)) {
                return AuthResult.Error("Неверный пароль")
            }

            if (!user.isActive) {
                return AuthResult.Error("Аккаунт деактивирован")
            }

            val currentTime = Date()
            userDao.updateLastLogin(user.id, currentTime)

            val session = sessionManager.createSession(user.id, isRememberMe)

            preferencesManager.saveSessionToken(session.sessionToken)
            preferencesManager.saveUserId(user.id)
            preferencesManager.setRememberMe(isRememberMe)

            AuthResult.Success(user.id)

        } catch (e: Exception) {
            AuthResult.Error("Ошибка входа: ${e.message}")
        }
    }

    override suspend fun logout() {
        try {
            val token = preferencesManager.getSessionToken().first()
            token?.let { sessionDao.deleteSession(it) }
            preferencesManager.clearSession()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun changePassword(
        userId: Long,
        currentPassword: String,
        newPassword: String
    ): AuthResult {
        return try {
            val user = userDao.getUserById(userId) ?: return AuthResult.Error("Пользователь не найден")

            if (!passwordManager.verifyPassword(currentPassword, user.salt, user.passwordHash)) {
                return AuthResult.Error("Неверный текущий пароль")
            }

            val passwordStrength = passwordManager.isPasswordStrong(newPassword)
            if (passwordStrength == PasswordStrength.WEAK) {
                return AuthResult.Error("Новый пароль слишком слабый")
            }

            val newSalt = passwordManager.generateSalt()
            val newPasswordHash = passwordManager.hashPassword(newPassword, newSalt)

            userDao.updatePassword(userId, newPasswordHash, newSalt)

            userDao.updateUser(user.copy(updatedAt = Date()))

            AuthResult.Success(userId, "Пароль успешно изменен")

        } catch (e: Exception) {
            AuthResult.Error("Ошибка смены пароля: ${e.message}")
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return try {
            val token = preferencesManager.getSessionToken().first()
            token != null && sessionManager.validateSession(token)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getCurrentUser(): UserEntity? {
        return try {
            val token = preferencesManager.getSessionToken().first()
            val userId = token?.let { sessionManager.getCurrentUser(it) }
            userId?.let { userDao.getUserById(it) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateUserProfile(
        userId: Long,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ): AuthResult {
        return try {
            val user = userDao.getUserById(userId) ?: return AuthResult.Error("Пользователь не найден")

            val updatedUser = user.copy(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                updatedAt = Date()
            )

            userDao.updateUser(updatedUser)
            AuthResult.Success(userId, "Профиль успешно обновлен")

        } catch (e: Exception) {
            AuthResult.Error("Ошибка обновления профиля: ${e.message}")
        }
    }

    override suspend fun deleteAccount(userId: Long, password: String): AuthResult {
        return try {
            val user = userDao.getUserById(userId) ?: return AuthResult.Error("Пользователь не найден")

            if (!passwordManager.verifyPassword(password, user.salt, user.passwordHash)) {
                return AuthResult.Error("Неверный пароль")
            }

            sessionDao.deleteAllUserSessions(userId)

            userDao.updateUserStatus(userId, false)

            preferencesManager.clearSession()

            AuthResult.Success(userId, "Аккаунт успешно удален")

        } catch (e: Exception) {
            AuthResult.Error("Ошибка удаления аккаунта: ${e.message}")
        }
    }
}