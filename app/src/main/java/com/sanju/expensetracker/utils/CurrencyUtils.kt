package com.sanju.expensetracker.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {

    fun formatAmount(amount: Double): String {

        val locale = Locale.Builder()
            .setLanguage("en")
            .setRegion("IN")
            .build()

        val formatter =
            NumberFormat.getCurrencyInstance(locale)

        return formatter.format(amount)
    }
}