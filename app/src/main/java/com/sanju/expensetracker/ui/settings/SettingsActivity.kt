package com.sanju.expensetracker.ui.settings

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sanju.expensetracker.databinding.ActivitySettingsBinding
import com.sanju.expensetracker.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.sanju.expensetracker.utils.Constants

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val settingsViewModel: SettingsViewModel by viewModels()

    private val currencies = listOf(
        Constants.CURRENCY_INR,
        Constants.CURRENCY_USD,
        Constants.CURRENCY_EUR,
        Constants.CURRENCY_GBP
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCurrencySpinner()
        setupListeners()
        observeViewModel()
    }

    private fun setupCurrencySpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            currencies
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spinnerCurrency.adapter = adapter
    }

    private fun setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.saveDarkModeEnabled(isChecked)
        }

        binding.spinnerCurrency.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCurrency = currencies[position].substringBefore(" ")
                    settingsViewModel.saveSelectedCurrency(selectedCurrency)
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                    // No action needed
                }
            }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.uiState.collect { uiState ->

                    if (binding.switchDarkMode.isChecked != uiState.isDarkModeEnabled) {
                        binding.switchDarkMode.isChecked = uiState.isDarkModeEnabled
                    }

                    val selectedIndex = currencies.indexOfFirst {
                        it.startsWith(uiState.selectedCurrency)
                    }

                    if (
                        selectedIndex >= 0 &&
                        binding.spinnerCurrency.selectedItemPosition != selectedIndex
                    ) {
                        binding.spinnerCurrency.setSelection(selectedIndex)
                    }
                }
            }
        }
    }
}