package com.sanju.expensetracker.ui.state

import com.sanju.expensetracker.data.model.DashboardStats
import com.sanju.expensetracker.data.model.Expense

data class ExpenseUiState(
    val expenses: List<Expense> = emptyList(),
    val income: Double = 0.0,
    val expense: Double = 0.0,
    val balance: Double = 0.0,
    val dashboardStats: DashboardStats = DashboardStats(),
    val isLoading: Boolean = false,
    val message: String = ""
)