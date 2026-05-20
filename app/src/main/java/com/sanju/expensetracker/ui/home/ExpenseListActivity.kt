package com.sanju.expensetracker.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanju.expensetracker.R
import com.sanju.expensetracker.data.model.Expense
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.databinding.ActivityExpenseListBinding
import kotlinx.coroutines.launch

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseListBinding
    private lateinit var expenseAdapter: ExpenseAdapter

    private val expenseRepository = ExpenseRepository()

    private var allExpenses: List<Expense> = emptyList()
    private var selectedFilter: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExpenseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedFilter = getString(R.string.all)

        setupRecyclerView()
        setupSearch()
        setupFilter()
        loadExpenses()
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(
            onEditClick = { expense ->
                openEditExpenseScreen(expense)
            },
            onDeleteClick = { expense ->
                deleteExpense(expense.id)
            }
        )

        binding.recyclerExpenses.layoutManager = LinearLayoutManager(this)
        binding.recyclerExpenses.adapter = expenseAdapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                applyFilters()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun setupFilter() {
        binding.rgFilter.setOnCheckedChangeListener { _, checkedId ->
            selectedFilter = when (checkedId) {
                binding.rbIncome.id -> getString(R.string.income)
                binding.rbExpense.id -> getString(R.string.expense)
                else -> getString(R.string.all)
            }

            applyFilters()
        }
    }

    private fun loadExpenses() {
        lifecycleScope.launch {
            val result = expenseRepository.getUserExpenses()

            result.onSuccess { expenses ->
                allExpenses = expenses
                applyFilters()
            }

            result.onFailure {
                Toast.makeText(
                    this@ExpenseListActivity,
                    it.message ?: getString(R.string.failed_to_load_expenses),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun applyFilters() {
        val searchText = binding.etSearch.text.toString().trim().lowercase()

        val filteredExpenses = allExpenses.filter { expense ->
            val matchesSearch =
                expense.title.lowercase().contains(searchText) ||
                        expense.category.lowercase().contains(searchText)

            val matchesType =
                selectedFilter == getString(R.string.all) ||
                        expense.type.equals(selectedFilter, ignoreCase = true)

            matchesSearch && matchesType
        }

        expenseAdapter.updateExpenses(filteredExpenses)
    }

    private fun openEditExpenseScreen(expense: Expense) {
        val intent = Intent(this, EditExpenseActivity::class.java)

        intent.putExtra("expenseId", expense.id)
        intent.putExtra("title", expense.title)
        intent.putExtra("amount", expense.amount)
        intent.putExtra("category", expense.category)
        intent.putExtra("type", expense.type)

        startActivity(intent)
    }

    private fun deleteExpense(expenseId: String) {
        lifecycleScope.launch {
            val result = expenseRepository.deleteExpense(expenseId)

            result.onSuccess {
                Toast.makeText(
                    this@ExpenseListActivity,
                    it,
                    Toast.LENGTH_SHORT
                ).show()

                loadExpenses()
            }

            result.onFailure {
                Toast.makeText(
                    this@ExpenseListActivity,
                    it.message ?: getString(R.string.delete_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}