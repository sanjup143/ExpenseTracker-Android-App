package com.sanju.expensetracker.ui.home

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sanju.expensetracker.R
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.databinding.ActivityMonthlySummaryBinding
import com.sanju.expensetracker.utils.CurrencyUtils
import kotlinx.coroutines.launch
import java.util.Calendar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MonthlySummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonthlySummaryBinding

    @Inject
    lateinit var expenseRepository: ExpenseRepository

    private val months = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMonthlySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMonthSpinner()
        setupDefaultYear()
        setupClickListeners()
        loadSelectedMonthSummary()
    }

    private fun setupMonthSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            months
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spinnerMonth.adapter = adapter

        val currentMonthIndex = Calendar.getInstance().get(Calendar.MONTH)
        binding.spinnerMonth.setSelection(currentMonthIndex)
    }

    private fun setupDefaultYear() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        binding.etYear.setText(currentYear.toString())
    }

    private fun setupClickListeners() {
        binding.btnLoadMonthlySummary.setOnClickListener {
            loadSelectedMonthSummary()
        }
    }

    private fun loadSelectedMonthSummary() {

        val selectedMonth = binding.spinnerMonth.selectedItemPosition + 1

        val selectedMonthName =
            months[binding.spinnerMonth.selectedItemPosition]

        val yearText =
            binding.etYear.text.toString().trim()

        if (yearText.isEmpty()) {
            binding.etYear.error =
                getString(R.string.year_required)

            return
        }

        val selectedYear = yearText.toInt()

        binding.btnLoadMonthlySummary.isEnabled = false

        binding.btnLoadMonthlySummary.text =
            getString(R.string.loading)

        lifecycleScope.launch {

            val result = expenseRepository.getMonthlySummary(
                month = selectedMonth,
                year = selectedYear
            )

            binding.btnLoadMonthlySummary.isEnabled = true

            binding.btnLoadMonthlySummary.text =
                getString(R.string.load_summary)

            result.onSuccess { summary ->

                val income = summary.first
                val expense = summary.second
                val balance = summary.third

                binding.tvSelectedMonth.text =
                    getString(
                        R.string.monthly_title,
                        selectedMonthName,
                        selectedYear
                    )

                binding.tvMonthlyIncome.text =
                    getString(
                        R.string.income_format,
                        CurrencyUtils.formatAmount(income)
                    )

                binding.tvMonthlyExpense.text =
                    getString(
                        R.string.expense_format,
                        CurrencyUtils.formatAmount(expense)
                    )

                binding.tvMonthlyBalance.text =
                    getString(
                        R.string.balance_format,
                        CurrencyUtils.formatAmount(balance)
                    )
            }

            result.onFailure {

                Toast.makeText(
                    this@MonthlySummaryActivity,
                    it.message
                        ?: getString(
                            R.string.failed_to_load_monthly_summary
                        ),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}