package com.narimukkil.pocketai.parser
import javax.inject.Inject

class MerchantExtractor @Inject constructor() {

    private val patterns = listOf(
        Regex("""(?i)(?:at|to|info-)\s+([A-Za-z0-9\s]+?)(?=\s+(?:on|via|ref|bal|available|\.))"""),
        Regex("""(?i)(?:at|to|info-)\s+([A-Za-z0-9\s]+)""")
    )

    fun extract(text: String): String? {
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null && match.groupValues.size > 1) {
                return match.groupValues[1].trim()
            }
        }
        return null
    }
}
