package com.sanju.expensetracker.data.model

data class DashboardStats(

    val totalTransactions: Int = 0,

    val highestIncome: Double = 0.0,

    val highestExpense: Double = 0.0,

    val averageIncome: Double = 0.0,

    val averageExpense: Double = 0.0,

    val thisMonthTransactions: Int = 0

)