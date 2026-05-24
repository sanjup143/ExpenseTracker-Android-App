package com.sanju.expensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanju.expensetracker.data.preferences.SettingsPreferences
import com.sanju.expensetracker.ui.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState()
    )

    val uiState: StateFlow<SettingsUiState> =
        _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsPreferences.isDarkModeEnabled.collect { isEnabled ->

                _uiState.value = _uiState.value.copy(
                    isDarkModeEnabled = isEnabled
                )
            }
        }

        viewModelScope.launch {
            settingsPreferences.selectedCurrency.collect { currency ->

                _uiState.value = _uiState.value.copy(
                    selectedCurrency = currency
                )
            }
        }
    }

    fun saveDarkModeEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsPreferences.saveDarkModeEnabled(
                isEnabled
            )
        }
    }

    fun saveSelectedCurrency(currency: String) {
        viewModelScope.launch {
            settingsPreferences.saveSelectedCurrency(
                currency
            )
        }
    }
}