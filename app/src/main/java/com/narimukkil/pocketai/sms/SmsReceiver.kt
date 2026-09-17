package com.narimukkil.pocketai.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            return
        }

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (message in messages) {
            val sender = message.displayOriginatingAddress
            val body = message.messageBody
            val timestamp = message.timestampMillis

            Log.d(
                "PocketAI_SMS",
                "SMS received from: $sender"
            )

            Log.d(
                "PocketAI_SMS",
                "SMS timestamp: $timestamp"
            )

            // Enqueue work
            val workData = workDataOf(
                SmsWorker.KEY_SMS_BODY to body,
                SmsWorker.KEY_SENDER to sender,
                SmsWorker.KEY_SMS_ID to timestamp.toString(),
                SmsWorker.KEY_TIMESTAMP to timestamp
            )

            val workRequest = OneTimeWorkRequestBuilder<SmsWorker>()
                .setInputData(workData)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}