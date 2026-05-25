package com.sanju.expensetracker.utils

object CategoryIconUtils {

    fun getCategoryIcon(category: String): String {

        return when (category.lowercase()) {

            "food" -> "🍔"

            "shopping" -> "🛍"

            "salary" -> "💼"

            "job" -> "💼"

            "travel" -> "✈️"

            "bills" -> "💡"

            "health" -> "🏥"

            "movie" -> "🎬"

            "education" -> "📚"

            "gym" -> "🏋️"

            "family" -> "👨‍👩‍👧"

            "gift" -> "🎁"

            "investment" -> "📈"

            "freelance" -> "💻"

            else -> "💰"
        }
    }
}