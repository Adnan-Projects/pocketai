package com.narimukkil.pocketai.parser

import javax.inject.Inject

class AccountExtractor @Inject constructor() {

    private val accountPatterns = listOf(
        Regex("""(?i)(?:a/c|acct|account|card)(?:\s+no\.?)?\s*(?:ending\s+with|x|ending)?\s*[*Xx]*(\d{4})"""),
        Regex("""(?i)[*Xx]+(\d{4})""")
    )

    private val bankPatterns = listOf(
        Regex("""(?i)-\s*([a-z\s]+bank|sbi|hdfc|icici|axis)"""),
        Regex("""(?i)(sbi|hdfc|icici|axis|federal bank)""")
    )

    fun extractLastFour(text: String): String? {
        for (pattern in accountPatterns) {
            val match = pattern.find(text)
            if (match != null && match.groupValues.size > 1) {
                return match.groupValues[1]
            }
        }
        return null
    }

    fun extractBankName(text: String): String? {
        for (pattern in bankPatterns) {
            val match = pattern.find(text)
            if (match != null && match.groupValues.size > 1) {
                return match.groupValues[1].trim().uppercase()
            }
        }
        return null
    }
}
