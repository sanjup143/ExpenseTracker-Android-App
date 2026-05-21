package com.sanju.expensetracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanju.expensetracker.data.model.Expense
import com.sanju.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val expenseRepository = ExpenseRepository(
        application.applicationContext
    )

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _summary = MutableStateFlow(Triple(0.0, 0.0, 0.0))
    val summary: StateFlow<Triple<Double, Double, Double>> = _summary.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadExpenses() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = expenseRepository.getUserExpenses()

            result.onSuccess {
                _expenses.value = it
            }

            result.onFailure {
                _message.value = it.message ?: "Failed to load expenses"
            }

            _isLoading.value = false
        }
    }

    fun loadSummary() {
        viewModelScope.launch {
            val result = expenseRepository.getExpenseSummary()

            result.onSuccess {
                _summary.value = it
            }

            result.onFailure {
                _message.value = it.message ?: "Failed to load summary"
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = expenseRepository.deleteExpense(expenseId)

            result.onSuccess {
                _message.value = it
                loadExpenses()
                loadSummary()
            }

            result.onFailure {
                _message.value = it.message ?: "Failed to delete expense"
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _message.value = ""
    }
}