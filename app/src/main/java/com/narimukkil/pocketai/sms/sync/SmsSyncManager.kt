package com.narimukkil.pocketai.sms.sync

import android.content.Context
import android.net.Uri
import android.provider.Telephony
import android.util.Log
import com.narimukkil.pocketai.data.repository.TransactionRepository
import com.narimukkil.pocketai.ml.CategoryClassifier
import com.narimukkil.pocketai.parser.SmsParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SmsSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val smsParser: SmsParser,
    private val transactionRepository: TransactionRepository,
    private val categoryClassifier: CategoryClassifier
) {
    suspend fun syncHistoricalSms(daysToLookBack: Int = 30): Int = withContext(Dispatchers.IO) {
        var processedCount = 0
        var insertedCount = 0
        val startTime = System.currentTimeMillis() - (daysToLookBack * 24L * 60L * 60L * 1000L)

        val uri = Telephony.Sms.Inbox.CONTENT_URI
        val projection = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE
        )
        val selection = "${Telephony.Sms.DATE} > ?"
        val selectionArgs = arrayOf(startTime.toString())
        val sortOrder = "${Telephony.Sms.DATE} DESC"

        val cursor = context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(Telephony.Sms._ID)
            val addressIndex = it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndexOrThrow(Telephony.Sms.DATE)

            while (it.moveToNext()) {
                val smsId = it.getString(idIndex)
                val address = it.getString(addressIndex)
                val body = it.getString(bodyIndex)
                val dateStr = it.getString(dateIndex)
                val timestamp = dateStr.toLongOrNull() ?: System.currentTimeMillis()

                var transaction = smsParser.parse(body, smsId, address, timestamp)
                if (transaction != null) {
                    val predictedCategory = categoryClassifier.categorize(transaction.merchant ?: "", transaction.type)
                    transaction = transaction.copy(category = predictedCategory)

                    val rawSmsHash = transaction.rawSmsHash
                    if (rawSmsHash != null) {
                        val existing = transactionRepository.findBySmsHash(rawSmsHash)
                        if (existing == null) {
                            // Sync might not extract the right date, use the SMS received date to be safe if extracting failed
                            val safeTransaction = if (transaction.transactionDate == 0L || transaction.transactionDate > System.currentTimeMillis()) {
                                transaction.copy(transactionDate = dateStr.toLongOrNull() ?: System.currentTimeMillis())
                            } else {
                                transaction
                            }
                            transactionRepository.insert(safeTransaction)
                            insertedCount++
                        }
                    }
                }
                processedCount++
            }
        }
        
        Log.d("SmsSyncManager", "Synced historical SMS. Processed: $processedCount, Inserted: $insertedCount")
        return@withContext insertedCount
    }
}
