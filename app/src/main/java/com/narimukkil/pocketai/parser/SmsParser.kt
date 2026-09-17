package com.narimukkil.pocketai.parser

import com.narimukkil.pocketai.data.local.entity.TransactionEntity

interface SmsParser {
    fun parse(smsBody: String, smsId: String, sender: String, timestamp: Long): TransactionEntity?
}
