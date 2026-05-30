package com.sanju.expensetracker.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sanju.expensetracker.R
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.databinding.ActivityEditExpenseBinding
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditExpenseBinding

    @Inject
    lateinit var expenseRepository: ExpenseRepository

    private var expenseId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        setupClickListeners()
    }

    private fun getIntentData() {
        expenseId = intent.getStringExtra("expenseId") ?: ""

        binding.etTitle.setText(intent.getStringExtra("title") ?: "")
        binding.etAmount.setText(
            intent.getDoubleExtra("amount", 0.0).toString()
        )
        binding.etCategory.setText(intent.getStringExtra("category") ?: "")

        val type = intent.getStringExtra("type") ?: getString(R.string.expense)

        if (type == getString(R.string.income)) {
            binding.rbIncome.isChecked = true
        } else {
            binding.rbExpense.isChecked = true
        }
    }

    private fun setupClickListeners() {
        binding.btnUpdateExpense.setOnClickListener {
            updateExpense()
        }
    }

    private fun updateExpense() {
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

        binding.btnUpdateExpense.isEnabled = false
        binding.btnUpdateExpense.text = getString(R.string.updating)

        lifecycleScope.launch {
            val result = expenseRepository.updateExpense(
                expenseId = expenseId,
                title = title,
                amount = amount,
                category = category,
                type = type
            )

            binding.btnUpdateExpense.isEnabled = true
            binding.btnUpdateExpense.text = getString(R.string.update)

            result.onSuccess {
                Toast.makeText(
                    this@EditExpenseActivity,
                    it,
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }

            result.onFailure {
                Toast.makeText(
                    this@EditExpenseActivity,
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}