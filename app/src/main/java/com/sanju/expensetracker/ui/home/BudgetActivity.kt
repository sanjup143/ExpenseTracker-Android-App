package com.sanju.expensetracker.ui.home

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sanju.expensetracker.R
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.databinding.ActivityBudgetBinding
import com.sanju.expensetracker.utils.CurrencyUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetBinding

    private val expenseRepository = ExpenseRepository()

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

        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMonthSpinner()
        setupDefaultYear()
        setupClickListeners()
        checkBudget()
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
        binding.btnSaveBudget.setOnClickListener {
            saveBudget()
        }
    }

    private fun saveBudget() {
        val month = binding.spinnerMonth.selectedItemPosition + 1
        val yearText = binding.etYear.text.toString().trim()
        val budgetText = binding.etBudgetAmount.text.toString().trim()

        if (yearText.isEmpty()) {
            binding.etYear.error = getString(R.string.year_required)
            return
        }

        if (budgetText.isEmpty()) {
            binding.etBudgetAmount.error = getString(R.string.budget_required)
            return
        }

        val year = yearText.toInt()
        val budgetAmount = budgetText.toDouble()

        binding.btnSaveBudget.isEnabled = false
        binding.btnSaveBudget.text = getString(R.string.saving)

        lifecycleScope.launch {
            val result = expenseRepository.saveMonthlyBudget(
                month = month,
                year = year,
                amount = budgetAmount
            )

            binding.btnSaveBudget.isEnabled = true
            binding.btnSaveBudget.text = getString(R.string.save_budget)

            result.onSuccess {
                Toast.makeText(
                    this@BudgetActivity,
                    it,
                    Toast.LENGTH_SHORT
                ).show()

                binding.etBudgetAmount.text?.clear()
                checkBudget()
            }

            result.onFailure {
                Toast.makeText(
                    this@BudgetActivity,
                    it.message ?: getString(R.string.failed_to_save_budget),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun checkBudget() {
        val month = binding.spinnerMonth.selectedItemPosition + 1
        val monthName = months[binding.spinnerMonth.selectedItemPosition]
        val yearText = binding.etYear.text.toString().trim()

        if (yearText.isEmpty()) {
            return
        }

        val year = yearText.toInt()

        lifecycleScope.launch {
            val budgetResult = expenseRepository.getMonthlyBudget(
                month = month,
                year = year
            )

            val summaryResult = expenseRepository.getMonthlySummary(
                month = month,
                year = year
            )

            if (budgetResult.isFailure || summaryResult.isFailure) {
                Toast.makeText(
                    this@BudgetActivity,
                    getString(R.string.failed_to_check_budget),
                    Toast.LENGTH_LONG
                ).show()
                return@launch
            }

            val budgetAmount = budgetResult.getOrNull() ?: 0.0
            val monthlyExpense = summaryResult.getOrNull()?.second ?: 0.0
            val remaining = budgetAmount - monthlyExpense

            binding.tvBudgetMonth.text =
                getString(R.string.selected_month_year, monthName, year)

            binding.tvBudgetLimit.text =
                getString(
                    R.string.budget_label,
                    CurrencyUtils.formatAmount(budgetAmount)
                )

            binding.tvSpent.text =
                getString(
                    R.string.spent_label,
                    CurrencyUtils.formatAmount(monthlyExpense)
                )

            binding.tvRemaining.text =
                getString(
                    R.string.remaining_label,
                    CurrencyUtils.formatAmount(remaining)
                )

            binding.tvBudgetStatus.text = when {
                budgetAmount == 0.0 -> getString(R.string.status_no_budget_set)
                remaining >= 0.0 -> getString(R.string.status_safe)
                else -> getString(R.string.status_over_budget)
            }
        }
    }
}