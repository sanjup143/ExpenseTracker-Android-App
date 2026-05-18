package com.sanju.expensetracker.data.model

data class Expense(
    val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val type: String = "",
    val userId: String = "",
    val month: Int = 0,
    val year: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)