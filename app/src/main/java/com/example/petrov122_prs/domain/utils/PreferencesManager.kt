package com.example.petrov122_prs.domain.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val SESSION_TOKEN = stringPreferencesKey("session_token")
        private val USER_ID = longPreferencesKey("user_id")
        private val REMEMBER_ME = booleanPreferencesKey("remember_me")
        private val LAST_LOGIN_TIME = stringPreferencesKey("last_login_time")
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }

    suspend fun saveSessionToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[SESSION_TOKEN] = token
        }
    }

    suspend fun saveUserId(userId: Long) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    suspend fun setRememberMe(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_ME] = enabled
        }
    }

    suspend fun saveLastLoginTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_LOGIN_TIME] = time
        }
    }

    suspend fun setFirstLaunch(isFirst: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = isFirst
        }
    }

    fun getSessionToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[SESSION_TOKEN]
        }
    }

    fun getUserId(): Flow<Long?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }
    }

    fun isRememberMeEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[REMEMBER_ME] ?: false
        }
    }

    fun getLastLoginTime(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[LAST_LOGIN_TIME]
        }
    }

    fun isFirstLaunch(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_FIRST_LAUNCH] ?: true
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(SESSION_TOKEN)
            preferences.remove(USER_ID)
            preferences[REMEMBER_ME] = false
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}