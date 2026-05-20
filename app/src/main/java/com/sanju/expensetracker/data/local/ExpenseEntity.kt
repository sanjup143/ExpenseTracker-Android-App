package com.sanju.expensetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(

    @PrimaryKey
    val id: String,

    val title: String,
    val amount: Double,
    val category: String,
    val type: String,
    val userId: String,
    val month: Int,
    val year: Int,
    val createdAt: Long
)