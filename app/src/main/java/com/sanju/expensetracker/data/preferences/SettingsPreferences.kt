package com.sanju.expensetracker.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(
    name = "settings_preferences"
)

class SettingsPreferences(
    private val context: Context
) {

    private val darkModeKey = booleanPreferencesKey("dark_mode_enabled")

    val isDarkModeEnabled: Flow<Boolean> =
        context.settingsDataStore.data.map { preferences ->
            preferences[darkModeKey] ?: false
        }

    suspend fun saveDarkModeEnabled(isEnabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[darkModeKey] = isEnabled
        }
    }
}