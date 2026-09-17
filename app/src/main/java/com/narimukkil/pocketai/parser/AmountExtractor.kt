package com.narimukkil.pocketai.parser

import java.util.Locale
import javax.inject.Inject

class AmountExtractor @Inject constructor() {

    private val amountPatterns = listOf(
        Regex("""(?:₹|RS\.?|INR)\s*([\d,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE),
        Regex("""(?:debited|credited|paid)(?:\s+by)?\s*(?:₹|RS\.?|INR)?\s*([\d,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
    )

    fun extract(text: String): Long? {
        var match: MatchResult? = null
        for (pattern in amountPatterns) {
            match = pattern.find(text)
            if (match != null) break
        }
        
        if (match == null) return null

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