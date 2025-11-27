package com.example.petrov122_prs.domain.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.tooltipDataStore: DataStore<Preferences> by preferencesDataStore(name = "tooltip_preferences")

/**
 * Manages tooltip display state across the application
 * Ensures tooltips are shown only once per user session or until dismissed
 */
class TooltipManager(private val context: Context) {

    companion object {
        // Tooltip preference keys
        private val TOOLTIP_LOGIN_IDENTIFIER = booleanPreferencesKey("tooltip_login_identifier")
        private val TOOLTIP_LOGIN_PASSWORD = booleanPreferencesKey("tooltip_login_password")
        private val TOOLTIP_LOGIN_REMEMBER_ME = booleanPreferencesKey("tooltip_login_remember_me")
        private val TOOLTIP_REGISTER_USERNAME = booleanPreferencesKey("tooltip_register_username")
        private val TOOLTIP_REGISTER_EMAIL = booleanPreferencesKey("tooltip_register_email")
        private val TOOLTIP_REGISTER_PASSWORD = booleanPreferencesKey("tooltip_register_password")
        private val TOOLTIP_REGISTER_CONFIRM_PASSWORD = booleanPreferencesKey("tooltip_register_confirm_password")
        private val TOOLTIP_DEMO_LOGIN = booleanPreferencesKey("tooltip_demo_login")
        private val TOOLTIP_GUEST_LOGIN = booleanPreferencesKey("tooltip_guest_login")
        private val TOOLTIP_PASSWORD_STRENGTH = booleanPreferencesKey("tooltip_password_strength")
        
        // Global tooltip control
        private val TOOLTIPS_ENABLED = booleanPreferencesKey("tooltips_enabled")
        private val SHOW_TOOLTIPS_FIRST_TIME = booleanPreferencesKey("show_tooltips_first_time")
    }

    /**
     * Check if a specific tooltip should be shown
     */
    suspend fun shouldShowTooltip(tooltipKey: String): Boolean {
        val enabled = isTooltipsEnabled()
        if (!enabled) return false
        
        val key = getPreferenceKey(tooltipKey)
        return context.tooltipDataStore.data.map { preferences ->
            preferences[key] ?: true // Show by default if not set
        }.first()
    }

    /**
     * Mark a tooltip as shown/dismissed
     */
    suspend fun markTooltipAsShown(tooltipKey: String) {
        val key = getPreferenceKey(tooltipKey)
        context.tooltipDataStore.edit { preferences ->
            preferences[key] = false
        }
    }

    /**
     * Check if tooltips are globally enabled
     */
    suspend fun isTooltipsEnabled(): Boolean {
        return context.tooltipDataStore.data.map { preferences ->
            preferences[TOOLTIPS_ENABLED] ?: true // Enabled by default
        }.first()
    }

    /**
     * Enable or disable all tooltips
     */
    suspend fun setTooltipsEnabled(enabled: Boolean) {
        context.tooltipDataStore.edit { preferences ->
            preferences[TOOLTIPS_ENABLED] = enabled
        }
    }

    /**
     * Check if this is the first time showing tooltips
     */
    fun isFirstTimeShowingTooltips(): Flow<Boolean> {
        return context.tooltipDataStore.data.map { preferences ->
            preferences[SHOW_TOOLTIPS_FIRST_TIME] ?: true
        }
    }

    /**
     * Mark that tooltips have been shown for the first time
     */
    suspend fun markFirstTimeTooltipsShown() {
        context.tooltipDataStore.edit { preferences ->
            preferences[SHOW_TOOLTIPS_FIRST_TIME] = false
        }
    }

    /**
     * Reset all tooltips to show again
     */
    suspend fun resetAllTooltips() {
        context.tooltipDataStore.edit { preferences ->
            preferences.clear()
            preferences[TOOLTIPS_ENABLED] = true
            preferences[SHOW_TOOLTIPS_FIRST_TIME] = true
        }
    }

    /**
     * Reset specific tooltip
     */
    suspend fun resetTooltip(tooltipKey: String) {
        val key = getPreferenceKey(tooltipKey)
        context.tooltipDataStore.edit { preferences ->
            preferences[key] = true
        }
    }

    /**
     * Get preference key for tooltip identifier
     */
    private fun getPreferenceKey(tooltipKey: String): Preferences.Key<Boolean> {
        return when (tooltipKey) {
            TooltipKeys.LOGIN_IDENTIFIER -> TOOLTIP_LOGIN_IDENTIFIER
            TooltipKeys.LOGIN_PASSWORD -> TOOLTIP_LOGIN_PASSWORD
            TooltipKeys.LOGIN_REMEMBER_ME -> TOOLTIP_LOGIN_REMEMBER_ME
            TooltipKeys.REGISTER_USERNAME -> TOOLTIP_REGISTER_USERNAME
            TooltipKeys.REGISTER_EMAIL -> TOOLTIP_REGISTER_EMAIL
            TooltipKeys.REGISTER_PASSWORD -> TOOLTIP_REGISTER_PASSWORD
            TooltipKeys.REGISTER_CONFIRM_PASSWORD -> TOOLTIP_REGISTER_CONFIRM_PASSWORD
            TooltipKeys.DEMO_LOGIN -> TOOLTIP_DEMO_LOGIN
            TooltipKeys.GUEST_LOGIN -> TOOLTIP_GUEST_LOGIN
            TooltipKeys.PASSWORD_STRENGTH -> TOOLTIP_PASSWORD_STRENGTH
            else -> booleanPreferencesKey(tooltipKey)
        }
    }
}

/**
 * Predefined tooltip keys for the application
 */
object TooltipKeys {
    const val LOGIN_IDENTIFIER = "login_identifier"
    const val LOGIN_PASSWORD = "login_password"
    const val LOGIN_REMEMBER_ME = "login_remember_me"
    const val REGISTER_USERNAME = "register_username"
    const val REGISTER_EMAIL = "register_email"
    const val REGISTER_PASSWORD = "register_password"
    const val REGISTER_CONFIRM_PASSWORD = "register_confirm_password"
    const val DEMO_LOGIN = "demo_login"
    const val GUEST_LOGIN = "guest_login"
    const val PASSWORD_STRENGTH = "password_strength"
}

/**
 * Tooltip content definitions
 */
object TooltipContent {
    const val LOGIN_IDENTIFIER = "Введите ваш email или имя пользователя"
    const val LOGIN_PASSWORD = "Введите пароль для входа в систему"
    const val LOGIN_REMEMBER_ME = "Оставайтесь в системе до 30 дней"
    const val REGISTER_USERNAME = "Уникальное имя, минимум 3 символа"
    const val REGISTER_EMAIL = "Используется для входа и восстановления пароля"
    const val REGISTER_PASSWORD = "Минимум 8 символов, включая цифры и заглавные буквы"
    const val REGISTER_CONFIRM_PASSWORD = "Повторите пароль для подтверждения"
    const val DEMO_LOGIN = "Быстрый вход с демо-учетными данными"
    const val GUEST_LOGIN = "Войти без регистрации (ограниченный функционал)"
    const val PASSWORD_STRENGTH = "Сильный пароль: 8+ символов, цифры, буквы разного регистра"
}