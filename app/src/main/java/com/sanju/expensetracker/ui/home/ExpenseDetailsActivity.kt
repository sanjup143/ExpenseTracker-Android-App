package com.sanju.expensetracker.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.databinding.ActivityExpenseDetailsBinding
import com.sanju.expensetracker.utils.CategoryIconUtils
import com.sanju.expensetracker.utils.CurrencyUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseDetailsBinding

    @Inject
    lateinit var expenseRepository: ExpenseRepository

    private var expenseId: String = ""
    private var title: String = ""
    private var amount: Double = 0.0
    private var category: String = ""
    private var type: String = ""
    private var createdAt: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExpenseDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        readIntentData()
        showExpenseDetails()
        setupClickListeners()
    }

    private fun readIntentData() {
        expenseId = intent.getStringExtra("expenseId").orEmpty()
        title = intent.getStringExtra("title").orEmpty()
        amount = intent.getDoubleExtra("amount", 0.0)
        category = intent.getStringExtra("category").orEmpty()
        type = intent.getStringExtra("type").orEmpty()
        createdAt = intent.getLongExtra("createdAt", System.currentTimeMillis())
    }

    private fun showExpenseDetails() {
        binding.tvCategoryIcon.text =
            CategoryIconUtils.getCategoryIcon(category)

        binding.tvTitle.text = title
        binding.tvAmount.text = CurrencyUtils.formatAmount(amount)
        binding.tvCategory.text = "Category: $category"
        binding.tvType.text = "Type: $type"
        binding.tvDate.text = "Date: ${formatDate(createdAt)}"
        binding.tvTime.text = "Time: ${formatTime(createdAt)}"
    }

    private fun setupClickListeners() {
        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, EditExpenseActivity::class.java)

            intent.putExtra("expenseId", expenseId)
            intent.putExtra("title", title)
            intent.putExtra("amount", amount)
            intent.putExtra("category", category)
            intent.putExtra("type", type)

            startActivity(intent)
            finish()
        }

        binding.btnDelete.setOnClickListener {
            deleteExpense()
        }
    }

    private fun deleteExpense() {
        lifecycleScope.launch {
            val result = expenseRepository.deleteExpense(expenseId)

            result.onSuccess {
                Toast.makeText(
                    this@ExpenseDetailsActivity,
                    it,
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }

            result.onFailure {
                Toast.makeText(
                    this@ExpenseDetailsActivity,
                    it.message ?: "Delete failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date(timestamp))
    }

    private fun formatTime(timestamp: Long): String {
        return SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        ).format(Date(timestamp))
    }
}