package com.sanju.expensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanju.expensetracker.data.model.DashboardStats
import com.sanju.expensetracker.data.repository.ExpenseRepository
import com.sanju.expensetracker.ui.state.ExpenseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.sanju.expensetracker.utils.Constants

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
            val statsResult = expenseRepository.getDashboardStats()

            if (
                expensesResult.isSuccess &&
                summaryResult.isSuccess &&
                statsResult.isSuccess
            ) {
                val expenses = expensesResult.getOrNull().orEmpty()
                val summary = summaryResult.getOrNull() ?: Triple(0.0, 0.0, 0.0)
                val dashboardStats = statsResult.getOrNull() ?: DashboardStats()

                _uiState.value = _uiState.value.copy(
                    expenses = expenses,
                    income = summary.first,
                    expense = summary.second,
                    balance = summary.third,
                    dashboardStats = dashboardStats,
                    isLoading = false,
                    message = ""
                )
            } else {
                val errorMessage =
                    expensesResult.exceptionOrNull()?.message
                        ?: summaryResult.exceptionOrNull()?.message
                        ?: statsResult.exceptionOrNull()?.message
                        ?: Constants.ERROR_LOAD_DASHBOARD_DATA

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
                    message = it.message ?: Constants.ERROR_DELETE_EXPENSE
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