package com.sanju.expensetracker.utils

object Constants {

    // Types
    const val TYPE_INCOME = "Income"
    const val TYPE_EXPENSE = "Expense"

    // Collections
    const val COLLECTION_EXPENSES = "expenses"
    const val COLLECTION_BUDGETS = "budgets"

    // Firestore Fields
    const val FIELD_USER_ID = "userId"
    const val FIELD_MONTH = "month"
    const val FIELD_YEAR = "year"
    const val FIELD_TITLE = "title"
    const val FIELD_AMOUNT = "amount"
    const val FIELD_CATEGORY = "category"
    const val FIELD_TYPE = "type"

    // Errors
    const val ERROR_USER_NOT_LOGGED_IN = "User not logged in"
    const val ERROR_LOAD_SUMMARY = "Failed to load summary"
    const val ERROR_LOAD_MONTHLY_SUMMARY = "Failed to load monthly summary"
    const val ERROR_LOAD_DASHBOARD_STATS = "Failed to load dashboard stats"
    const val ERROR_LOAD_CATEGORY_DATA = "Failed to load category data"
    const val ERROR_LOAD_DASHBOARD_DATA = "Failed to load dashboard data"
    const val ERROR_DELETE_EXPENSE = "Failed to delete expense"

    // Success Messages
    const val SUCCESS_EXPENSE_SAVED = "Expense saved successfully"
    const val SUCCESS_EXPENSE_DELETED = "Expense deleted successfully"
    const val SUCCESS_EXPENSE_UPDATED = "Expense updated successfully"
    const val SUCCESS_BUDGET_SAVED = "Budget saved successfully"

    // Currency Display Names
    const val CURRENCY_INR = "₹ INR"
    const val CURRENCY_USD = "$ USD"
    const val CURRENCY_EUR = "€ EUR"
    const val CURRENCY_GBP = "£ GBP"

    // Currency Symbols
    const val SYMBOL_INR = "₹"
    const val SYMBOL_USD = "$"
    const val SYMBOL_EUR = "€"
    const val SYMBOL_GBP = "£"

    const val REMINDER_NOTIFICATION_ID = 1001

    const val REMINDER_REQUEST_CODE = 1001
}