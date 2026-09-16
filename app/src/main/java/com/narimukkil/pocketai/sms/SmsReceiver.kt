package com.narimukkil.pocketai.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

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

            // Parsing will be added in the next step.
            // Do NOT store the raw SMS yet.
        }
    }
}