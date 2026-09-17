package com.narimukkil.pocketai.parser
import javax.inject.Inject

class TransactionTypeExtractor @Inject constructor() {

    fun extract(text: String): String {
        val lowerText = text.lowercase()
        return when {
            lowerText.contains("debited") || lowerText.contains("spent") || 
            lowerText.contains("paid") || lowerText.contains("sent") ||
            lowerText.contains("deducted") -> "EXPENSE"
            
            lowerText.contains("credited") || lowerText.contains("received") || 
            lowerText.contains("refund") || lowerText.contains("added") -> "INCOME"
            
            else -> "UNKNOWN"
        }
    }
}
