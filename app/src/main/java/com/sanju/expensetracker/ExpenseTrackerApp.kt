package com.sanju.expensetracker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.sanju.expensetracker.data.preferences.SettingsPreferences
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ExpenseTrackerApp : Application() {

    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    private val applicationScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main
    )

    override fun onCreate() {
        super.onCreate()

        observeDarkModePreference()
    }

    private fun observeDarkModePreference() {
        applicationScope.launch {
            settingsPreferences.isDarkModeEnabled.collect { isEnabled ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isEnabled) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_NO
                    }
                )
            }
        }
    }
}