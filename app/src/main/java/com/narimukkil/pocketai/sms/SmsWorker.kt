package com.narimukkil.pocketai.sms

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.narimukkil.pocketai.data.repository.TransactionRepository
import com.narimukkil.pocketai.parser.SmsParser
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import android.util.Log
import com.narimukkil.pocketai.ml.CategoryClassifier

@HiltWorker
class SmsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val smsParser: SmsParser,
    private val transactionRepository: TransactionRepository,
    private val categoryClassifier: CategoryClassifier
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val smsBody = inputData.getString(KEY_SMS_BODY) ?: return Result.failure()
        val sender = inputData.getString(KEY_SENDER) ?: return Result.failure()
        val smsId = inputData.getString(KEY_SMS_ID) ?: System.currentTimeMillis().toString()
        val timestamp = inputData.getLong(KEY_TIMESTAMP, System.currentTimeMillis())

        try {
            var transaction = smsParser.parse(smsBody, smsId, sender, timestamp)
            if (transaction != null) {
                // Classify using ML heuristics & user learning mappings
                val predictedCategory = categoryClassifier.categorize(transaction.merchant ?: "", transaction.type)
                transaction = transaction.copy(category = predictedCategory)

                // If the smsHash already exists, we might want to handle it (e.g. ignore or update)
                // Assuming replace strategy is handled in DAO or we check here
                val existing = transaction.rawSmsHash?.let { transactionRepository.findBySmsHash(it) }
                if (existing == null) {
                    transactionRepository.insert(transaction)
                    Log.d("SmsWorker", "Inserted transaction: $transaction")
                } else {
                    Log.d("SmsWorker", "Duplicate transaction ignored: ${transaction.rawSmsHash}")
                }
            } else {
                Log.d("SmsWorker", "Could not parse SMS into transaction")
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("SmsWorker", "Error processing SMS", e)
            return Result.retry()
        }
    }

    companion object {
        const val KEY_SMS_BODY = "sms_body"
        const val KEY_SENDER = "sender"
        const val KEY_SMS_ID = "sms_id"
        const val KEY_TIMESTAMP = "sms_timestamp"
    }
}
