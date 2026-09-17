package com.narimukkil.pocketai.parser

import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class DateExtractor @Inject constructor() {

    private val datePatterns = listOf(
        Regex("""(\d{2}[/-]\d{2}[/-]\d{2,4}\s+\d{2}:\d{2}:\d{2})"""),
        Regex("""(\d{2}[/-]\d{2}[/-]\d{2,4}\s+\d{2}:\d{2})"""),
        Regex("""(\d{2}[/-]\d{2}[/-]\d{2,4})"""),
        Regex("""(?i)(\d{2}[A-Z]{3}\d{2,4}\s+\d{2}:\d{2}:\d{2})"""),
        Regex("""(?i)(\d{2}[A-Z]{3}\d{2,4}\s+\d{2}:\d{2})"""),
        Regex("""(?i)(\d{2}[A-Z]{3}\d{2,4})""")
    )
    
    private val dateFormats = listOf(
        SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("dd-MM-yy HH:mm", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()),
        SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()),
        SimpleDateFormat("dd-MM-yy", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yy", Locale.getDefault()),
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
        SimpleDateFormat("ddMMMyy HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("ddMMMyyyy HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("ddMMMyy HH:mm", Locale.getDefault()),
        SimpleDateFormat("ddMMMyyyy HH:mm", Locale.getDefault()),
        SimpleDateFormat("ddMMMyy", Locale.getDefault()),
        SimpleDateFormat("ddMMMyyyy", Locale.getDefault())
    )

    fun extract(text: String): Long? {
        for (pattern in datePatterns) {
            val match = pattern.find(text)
            if (match != null) {
                val dateStr = match.groupValues[1]
                for (format in dateFormats) {
                    try {
                        val date = format.parse(dateStr)
                        if (date != null) {
                            return date.time
                        }
                    } catch (e: Exception) {
                        // Ignore and try the next format
                    }
                }
            }
        }
        return null
    }
}
