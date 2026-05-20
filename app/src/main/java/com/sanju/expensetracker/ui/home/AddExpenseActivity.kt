package com.sanju.expensetracker.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sanju.expensetracker.R
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.databinding.ActivityAddExpenseBinding
import kotlinx.coroutines.launch

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding

    private lateinit var expenseRepository: ExpenseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        expenseRepository = ExpenseRepository(applicationContext)

        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSaveExpense.setOnClickListener {
            validateExpense()
        }
    }

    private fun validateExpense() {

        val title = binding.etTitle.text.toString().trim()
        val amountText = binding.etAmount.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()

        if (title.isEmpty()) {
            binding.etTitle.error = getString(R.string.title_required)
            return
        }

        if (amountText.isEmpty()) {
            binding.etAmount.error = getString(R.string.amount_required)
            return
        }

        if (category.isEmpty()) {
            binding.etCategory.error = getString(R.string.category_required)
            return
        }

        val amount = amountText.toDouble()

        val type = if (binding.rbIncome.isChecked) {
            getString(R.string.income)
        } else {
            getString(R.string.expense)
        }

        saveExpense(
            title = title,
            amount = amount,
            category = category,
            type = type
        )
    }

    private fun saveExpense(
        title: String,
        amount: Double,
        category: String,
        type: String
    ) {

        binding.btnSaveExpense.isEnabled = false
        binding.btnSaveExpense.text = getString(R.string.saving)

        lifecycleScope.launch {

            val result = expenseRepository.addExpense(
                title = title,
                amount = amount,
                category = category,
                type = type
            )

            binding.btnSaveExpense.isEnabled = true
            binding.btnSaveExpense.text = getString(R.string.save)

            result.onSuccess {

                Toast.makeText(
                    this@AddExpenseActivity,
                    it,
                    Toast.LENGTH_SHORT
                ).show()

                clearFields()
            }

            result.onFailure {

                Toast.makeText(
                    this@AddExpenseActivity,
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun clearFields() {
        binding.etTitle.text?.clear()
        binding.etAmount.text?.clear()
        binding.etCategory.text?.clear()

        binding.rbExpense.isChecked = true
    }
}