package com.sanju.expensetracker.data.local

import com.sanju.expensetracker.data.model.Expense

object ExpenseMapper {

    fun toEntity(expense: Expense): ExpenseEntity {

        return ExpenseEntity(
            id = expense.id,
            title = expense.title,
            amount = expense.amount,
            category = expense.category,
            type = expense.type,
            userId = expense.userId,
            month = expense.month,
            year = expense.year,
            createdAt = expense.createdAt
        )
    }

    fun toExpense(entity: ExpenseEntity): Expense {

        return Expense(
            id = entity.id,
            title = entity.title,
            amount = entity.amount,
            category = entity.category,
            type = entity.type,
            userId = entity.userId,
            month = entity.month,
            year = entity.year,
            createdAt = entity.createdAt
        )
    }
}