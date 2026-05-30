package com.sanju.expensetracker.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {

    fun formatAmount(
        amount: Double,
        currencySymbol: String = Constants.SYMBOL_INR
    ): String {

        val locale = when (currencySymbol) {

            Constants.SYMBOL_USD -> Locale.US
            Constants.SYMBOL_EUR -> Locale.GERMANY
            Constants.SYMBOL_GBP -> Locale.UK

            else -> Locale.Builder()
                .setLanguage("en")
                .setRegion("IN")
                .build()
        }

        val formatter =
            NumberFormat.getCurrencyInstance(locale)

        return formatter.format(amount)
    }
}