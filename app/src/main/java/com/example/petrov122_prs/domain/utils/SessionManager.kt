package com.example.petrov122_prs.domain.utils

import android.content.Context
import android.provider.Settings
import com.example.petrov122_prs.data.dao.SessionDao
import com.example.petrov122_prs.data.entities.UserSessionEntity
import java.util.*
import javax.inject.Inject

class SessionManager @Inject constructor(
    private val sessionDao: SessionDao,
    private val context: Context
) {

    fun generateSessionToken(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    suspend fun createSession(
        userId: Long,
        isRememberMe: Boolean = false
    ): UserSessionEntity {
        val calendar = Calendar.getInstance()
        val loginTime = calendar.time

        calendar.apply {
            if (isRememberMe) {
                add(Calendar.DAY_OF_MONTH, 30)
            } else {
                add(Calendar.HOUR_OF_DAY, 24)
            }
        }

        val expiryTime = calendar.time

        val session = UserSessionEntity(
            sessionToken = generateSessionToken(),
            userId = userId,
            deviceId = getDeviceId(),
            loginTime = loginTime,
            expiryTime = expiryTime,
            isRememberMe = isRememberMe
        )

        sessionDao.insertSession(session)
        return session
    }

    suspend fun validateSession(token: String): Boolean {
        val session = sessionDao.getSessionByToken(token) ?: return false
        return session.expiryTime.after(Date())
    }

    suspend fun getCurrentUser(token: String): Long? {
        val session = sessionDao.getSessionByToken(token) ?: return null
        return if (session.expiryTime.after(Date())) {
            session.userId
        } else {
            sessionDao.deleteSession(token)
            null
        }
    }

    private fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    suspend fun cleanupExpiredSessions() {
        sessionDao.deleteExpiredSessions(Date())
    }

    suspend fun getUserSessions(userId: Long): List<UserSessionEntity> {
        return sessionDao.getRecentSessions(userId)
    }
}