package com.sanju.expensetracker.data.model

data class Budget(
    val id: String = "",
    val userId: String = "",
    val month: Int = 0,
    val year: Int = 0,
    val amount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)