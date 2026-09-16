package com.narimukkil.pocketai.parser

import java.util.Locale

class AmountExtractor {

    private val amountPattern = Regex(
        pattern = """(?:₹|RS\.?|INR)\s*([\d,]+(?:\.\d{1,2})?)""",
        option = RegexOption.IGNORE_CASE
    )

    fun extract(text: String): Long? {

        val match = amountPattern.find(text) ?: return null

        val amountText = match.groupValues[1]
            .replace(",", "")

        return try {
            val parts = amountText.split(".")

            val rupees = parts[0].toLong()

            val paise = when {
                parts.size == 1 -> 0L
                parts[1].length == 1 -> {
                    parts[1].toLong() * 10
                }
                else -> {
                    parts[1].take(2).toLong()
                }
            }

            rupees * 100 + paise

        } catch (e: NumberFormatException) {
            null
        }
    }
}