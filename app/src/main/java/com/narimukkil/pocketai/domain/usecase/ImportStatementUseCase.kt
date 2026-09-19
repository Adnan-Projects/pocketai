package com.narimukkil.pocketai.domain.usecase

import android.content.Context
import android.net.Uri
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import com.narimukkil.pocketai.data.repository.TransactionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImportStatementUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(uri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        try {
            var importedCount = 0
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
                val lines = reader.readLines()
                if (lines.isEmpty()) return@withContext Result.success(0)

                // Skip header line
                for (i in 1 until lines.size) {
                    val line = lines[i]
                    if (line.isBlank()) continue
                    
                    // Simple CSV split (won't handle commas inside quotes, but fits our export format for now)
                    val parts = line.split(",")
                    if (parts.size < 7) continue 

                    // Expected Format: Month,Date,Merchant,Category,Type,Amount,Payment Method
                    val dateStr = parts[1]
                    val merchant = parts[2]
                    val category = parts[3]
                    val type = parts[4]
                    val amountStr = parts[5]
                    val paymentMethod = parts[6]

                    val amountFloat = amountStr.toFloatOrNull() ?: continue
                    val amountPaise = (amountFloat * 100).toLong()

                    val timestamp = try {
                        dateFormat.parse(dateStr)?.time ?: continue
                    } catch (e: Exception) { continue }

                    // Deduplication window: +/- 24 hours
                    val windowStart = timestamp - 86400000L
                    val windowEnd = timestamp + 86400000L
                    val duplicate = repository.findDuplicate(amountPaise, type, windowStart, windowEnd)

                    if (duplicate == null) {
                        val rawHash = hashString("IMPORT$dateStr$merchant$amountPaise$type")
                        val transaction = TransactionEntity(
                            amountPaise = amountPaise,
                            type = type,
                            merchant = merchant.ifBlank { "Unknown" },
                            category = category,
                            transactionDate = timestamp,
                            paymentMethod = paymentMethod,
                            accountLastFour = null,
                            referenceNumber = null,
                            smsId = null,
                            rawSmsHash = rawHash,
                            confidence = 1.0f,
                            source = "IMPORT",
                            isReviewed = true
                        )
                        repository.insert(transaction)
                        importedCount++
                    }
                }
            }
            Result.success(importedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
