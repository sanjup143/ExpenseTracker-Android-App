package com.sanju.expensetracker.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanju.expensetracker.data.local.ExpenseDatabase
import com.sanju.expensetracker.data.local.ExpenseMapper
import com.sanju.expensetracker.data.model.Budget
import com.sanju.expensetracker.data.model.Expense
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class ExpenseRepository(
    context: Context
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val expenseDao = ExpenseDatabase
        .getDatabase(context)
        .expenseDao()

    suspend fun addExpense(
        title: String,
        amount: Double,
        category: String,
        type: String
    ): Result<String> {

        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val documentRef = firestore.collection("expenses").document()

            val calendar = Calendar.getInstance()
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)

            val expense = Expense(
                id = documentRef.id,
                title = title,
                amount = amount,
                category = category,
                type = type,
                userId = userId,
                month = month,
                year = year,
                createdAt = System.currentTimeMillis()
            )

            firestore.collection("expenses")
                .document(expense.id)
                .set(expense)
                .await()

            expenseDao.insertExpense(
                ExpenseMapper.toEntity(expense)
            )

            Result.success("Expense saved successfully")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserExpenses(): Result<List<Expense>> {

        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val snapshot = firestore.collection("expenses")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val expenses = snapshot.toObjects(Expense::class.java)
                .sortedByDescending { it.createdAt }

            expenseDao.clearExpenses()
            expenseDao.insertExpenses(
                expenses.map { ExpenseMapper.toEntity(it) }
            )

            Result.success(expenses)

        } catch (e: Exception) {
            val localExpenses = expenseDao.getAllExpenses()
                .map { ExpenseMapper.toExpense(it) }

            if (localExpenses.isNotEmpty()) {
                Result.success(localExpenses)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteExpense(expenseId: String): Result<String> {

        return try {
            firestore.collection("expenses")
                .document(expenseId)
                .delete()
                .await()

            expenseDao.deleteExpense(expenseId)

            Result.success("Expense deleted successfully")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateExpense(
        expenseId: String,
        title: String,
        amount: Double,
        category: String,
        type: String
    ): Result<String> {

        return try {
            val updates = mapOf(
                "title" to title,
                "amount" to amount,
                "category" to category,
                "type" to type
            )

            firestore.collection("expenses")
                .document(expenseId)
                .update(updates)
                .await()

            val oldExpense = expenseDao.getExpenseById(expenseId)

            if (oldExpense != null) {
                val updatedExpense = oldExpense.copy(
                    title = title,
                    amount = amount,
                    category = category,
                    type = type
                )

                expenseDao.insertExpense(updatedExpense)
            }

            Result.success("Expense updated successfully")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpenseSummary(): Result<Triple<Double, Double, Double>> {

        val expensesResult = getUserExpenses()

        return if (expensesResult.isSuccess) {
            Result.success(
                calculateSummary(expensesResult.getOrNull().orEmpty())
            )
        } else {
            Result.failure(
                expensesResult.exceptionOrNull()
                    ?: Exception("Failed to load summary")
            )
        }
    }

    suspend fun getMonthlyExpenses(
        month: Int,
        year: Int
    ): Result<List<Expense>> {

        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val snapshot = firestore.collection("expenses")
                .whereEqualTo("userId", userId)
                .whereEqualTo("month", month)
                .whereEqualTo("year", year)
                .get()
                .await()

            val expenses = snapshot.toObjects(Expense::class.java)
                .sortedByDescending { it.createdAt }

            Result.success(expenses)

        } catch (e: Exception) {
            val localExpenses = expenseDao.getAllExpenses()
                .map { ExpenseMapper.toExpense(it) }
                .filter { it.month == month && it.year == year }
                .sortedByDescending { it.createdAt }

            if (localExpenses.isNotEmpty()) {
                Result.success(localExpenses)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getMonthlySummary(
        month: Int,
        year: Int
    ): Result<Triple<Double, Double, Double>> {

        val monthlyExpensesResult = getMonthlyExpenses(
            month = month,
            year = year
        )

        return if (monthlyExpensesResult.isSuccess) {
            Result.success(
                calculateSummary(monthlyExpensesResult.getOrNull().orEmpty())
            )
        } else {
            Result.failure(
                monthlyExpensesResult.exceptionOrNull()
                    ?: Exception("Failed to load monthly summary")
            )
        }
    }

    private fun calculateSummary(
        expenses: List<Expense>
    ): Triple<Double, Double, Double> {

        var totalIncome = 0.0
        var totalExpense = 0.0

        expenses.forEach { expense ->
            if (expense.type == "Income") {
                totalIncome += expense.amount
            } else {
                totalExpense += expense.amount
            }
        }

        val balance = totalIncome - totalExpense

        return Triple(totalIncome, totalExpense, balance)
    }

    suspend fun saveMonthlyBudget(
        month: Int,
        year: Int,
        amount: Double
    ): Result<String> {

        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val budgetId = "${userId}_${month}_$year"

            val budget = Budget(
                id = budgetId,
                userId = userId,
                month = month,
                year = year,
                amount = amount
            )

            firestore.collection("budgets")
                .document(budgetId)
                .set(budget)
                .await()

            Result.success("Budget saved successfully")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMonthlyBudget(
        month: Int,
        year: Int
    ): Result<Double> {

        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val budgetId = "${userId}_${month}_$year"

            val document = firestore.collection("budgets")
                .document(budgetId)
                .get()
                .await()

            val budget = document.toObject(Budget::class.java)

            Result.success(budget?.amount ?: 0.0)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpenseByCategory(): Result<Map<String, Double>> {

        val expensesResult = getUserExpenses()

        return if (expensesResult.isSuccess) {
            val categoryMap = mutableMapOf<String, Double>()

            expensesResult.getOrNull().orEmpty().forEach { expense ->
                if (expense.type == "Expense") {
                    val currentAmount = categoryMap[expense.category] ?: 0.0
                    categoryMap[expense.category] = currentAmount + expense.amount
                }
            }

            Result.success(categoryMap)
        } else {
            Result.failure(
                expensesResult.exceptionOrNull()
                    ?: Exception("Failed to load category data")
            )
        }
    }
}