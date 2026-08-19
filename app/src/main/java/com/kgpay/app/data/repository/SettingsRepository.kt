package com.kgpay.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "kgpay_settings")

enum class AppLanguage { ENGLISH, HINDI }

data class AppSettings(
    val onboardingComplete: Boolean = false,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val darkMode: Boolean = false,
    val appLockEnabled: Boolean = false,
    val lowBalanceAlerts: Boolean = true,
    val simSlot: Int = 0,
)

/** All user-facing preferences added in the PM-lens pass: language, theme, app-lock, alerts, SIM. */
class SettingsRepository(context: Context) {
    private val store = context.dataStore

    private object Keys {
        val ONBOARDING = booleanPreferencesKey("onboarding_complete")
        val LANGUAGE = stringPreferencesKey("language")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val APP_LOCK = booleanPreferencesKey("app_lock_enabled")
        val LOW_BALANCE_ALERTS = booleanPreferencesKey("low_balance_alerts")
        val SIM_SLOT = intPreferencesKey("sim_slot")
    }

    val settings: Flow<AppSettings> = store.data.map { prefs ->
        AppSettings(
            onboardingComplete = prefs[Keys.ONBOARDING] ?: false,
            language = runCatching { AppLanguage.valueOf(prefs[Keys.LANGUAGE] ?: "ENGLISH") }.getOrDefault(AppLanguage.ENGLISH),
            darkMode = prefs[Keys.DARK_MODE] ?: false,
            appLockEnabled = prefs[Keys.APP_LOCK] ?: false,
            lowBalanceAlerts = prefs[Keys.LOW_BALANCE_ALERTS] ?: true,
            simSlot = prefs[Keys.SIM_SLOT] ?: 0,
        )
    }

    suspend fun setOnboardingComplete(done: Boolean) = store.edit { it[Keys.ONBOARDING] = done }
    suspend fun setLanguage(language: AppLanguage) = store.edit { it[Keys.LANGUAGE] = language.name }
    suspend fun setDarkMode(enabled: Boolean) = store.edit { it[Keys.DARK_MODE] = enabled }
    suspend fun setAppLockEnabled(enabled: Boolean) = store.edit { it[Keys.APP_LOCK] = enabled }
    suspend fun setLowBalanceAlerts(enabled: Boolean) = store.edit { it[Keys.LOW_BALANCE_ALERTS] = enabled }
    suspend fun setSimSlot(slot: Int) = store.edit { it[Keys.SIM_SLOT] = slot }
}
