package com.sanju.expensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.ui.state.ExpenseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true
            )

            val expensesResult = expenseRepository.getUserExpenses()
            val summaryResult = expenseRepository.getExpenseSummary()

            if (expensesResult.isSuccess && summaryResult.isSuccess) {
                val expenses = expensesResult.getOrNull().orEmpty()
                val summary = summaryResult.getOrNull() ?: Triple(0.0, 0.0, 0.0)

                _uiState.value = _uiState.value.copy(
                    expenses = expenses,
                    income = summary.first,
                    expense = summary.second,
                    balance = summary.third,
                    isLoading = false,
                    message = ""
                )
            } else {
                val errorMessage =
                    expensesResult.exceptionOrNull()?.message
                        ?: summaryResult.exceptionOrNull()?.message
                        ?: "Failed to load dashboard data"

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = errorMessage
                )
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true
            )

            val result = expenseRepository.deleteExpense(expenseId)

            result.onSuccess { message ->
                _uiState.value = _uiState.value.copy(
                    message = message
                )

                loadDashboardData()
            }

            result.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = it.message ?: "Failed to delete expense"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            message = ""
        )
    }
}