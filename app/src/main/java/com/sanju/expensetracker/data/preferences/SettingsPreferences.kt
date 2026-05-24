package com.sanju.expensetracker.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(
    name = "settings_preferences"
)

class SettingsPreferences(
    private val context: Context
) {

    private val darkModeKey = booleanPreferencesKey(
        "dark_mode_enabled"
    )

    private val currencyKey = stringPreferencesKey(
        "selected_currency"
    )

    val isDarkModeEnabled: Flow<Boolean> =
        context.settingsDataStore.data.map { preferences ->
            preferences[darkModeKey] ?: false
        }

    val selectedCurrency: Flow<String> =
        context.settingsDataStore.data.map { preferences ->
            preferences[currencyKey] ?: "₹"
        }

    suspend fun saveDarkModeEnabled(isEnabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[darkModeKey] = isEnabled
        }
    }

    suspend fun saveSelectedCurrency(currency: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[currencyKey] = currency
        }
    }
}