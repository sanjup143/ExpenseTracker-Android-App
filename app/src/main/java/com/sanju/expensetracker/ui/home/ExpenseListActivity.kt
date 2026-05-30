package com.sanju.expensetracker.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanju.expensetracker.R
import com.sanju.expensetracker.data.model.Expense
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.databinding.ActivityExpenseListBinding
import com.sanju.expensetracker.ui.adapter.ExpenseAdapter
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseListBinding
    private lateinit var expenseAdapter: ExpenseAdapter

    @Inject
    lateinit var expenseRepository: ExpenseRepository

    private var allExpenses: List<Expense> = emptyList()
    private var selectedFilter: String = ""
    private var selectedCategory: String = ""
    private var selectedSort: String = ""

    private val sortOptions by lazy {
        listOf(
            getString(R.string.latest_first),
            getString(R.string.oldest_first),
            getString(R.string.amount_high_to_low),
            getString(R.string.amount_low_to_high)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExpenseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedCategory = getString(R.string.all_categories)
        selectedSort = getString(R.string.latest_first)

        selectedFilter = getString(R.string.all)

        setupRecyclerView()
        setupSearch()
        setupFilter()
        setupSortSpinner()
        loadExpenses()
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(
            onItemClick = { expense ->
                openExpenseDetailsScreen(expense)
            },
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

            setupCategorySpinner()
            applyFilters()
        }
    }

    private fun setupCategorySpinner() {
        val categories = mutableListOf(
            getString(R.string.all_categories)
        )

        categories.addAll(
            allExpenses
                .filter {
                    selectedFilter == getString(R.string.all) ||
                            it.type.equals(selectedFilter, ignoreCase = true)
                }
                .map { it.category }
                .distinct()
                .sorted()
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spinnerCategory.adapter = adapter

        if (!categories.contains(selectedCategory)) {
            selectedCategory = getString(R.string.all_categories)
        }

        val selectedIndex = categories.indexOf(selectedCategory)
        if (selectedIndex >= 0) {
            binding.spinnerCategory.setSelection(selectedIndex)
        }

        binding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCategory = categories[position]
                    applyFilters()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
    }

    private fun setupSortSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            sortOptions
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spinnerSort.adapter = adapter

        binding.spinnerSort.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSort = sortOptions[position]
                    applyFilters()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
    }

    private fun loadExpenses() {
        lifecycleScope.launch {
            val result = expenseRepository.getUserExpenses()

            result.onSuccess { expenses ->
                allExpenses = expenses
                setupCategorySpinner()
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

        val filteredExpenses = allExpenses
            .filter { expense ->
                val matchesSearch =
                    expense.title.lowercase().contains(searchText) ||
                            expense.category.lowercase().contains(searchText)

                val matchesType =
                    selectedFilter == getString(R.string.all) ||
                            expense.type.equals(selectedFilter, ignoreCase = true)

                val matchesCategory =
                    selectedCategory == getString(R.string.all_categories) ||
                            expense.category.equals(selectedCategory, ignoreCase = true)

                matchesSearch && matchesType && matchesCategory
            }
            .let { expenses ->
                when (selectedSort) {
                    getString(R.string.oldest_first) -> expenses.sortedBy { it.createdAt }
                    getString(R.string.amount_high_to_low) -> expenses.sortedByDescending { it.amount }
                    getString(R.string.amount_low_to_high) -> expenses.sortedBy { it.amount }
                    else -> expenses.sortedByDescending { it.createdAt }
                }
            }

        expenseAdapter.updateExpenses(filteredExpenses)

        if (filteredExpenses.isEmpty()) {
            binding.recyclerExpenses.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.recyclerExpenses.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        }
    }

    private fun openExpenseDetailsScreen(expense: Expense) {
        val intent = Intent(this, ExpenseDetailsActivity::class.java)

        intent.putExtra("expenseId", expense.id)
        intent.putExtra("title", expense.title)
        intent.putExtra("amount", expense.amount)
        intent.putExtra("category", expense.category)
        intent.putExtra("type", expense.type)
        intent.putExtra("createdAt", expense.createdAt)

        startActivity(intent)
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