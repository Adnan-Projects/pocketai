package com.narimukkil.pocketai.parser

import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import javax.inject.Inject
import java.security.MessageDigest

class RegexSmsParserImpl @Inject constructor(
    private val amountExtractor: AmountExtractor,
    private val merchantExtractor: MerchantExtractor,
    private val transactionTypeExtractor: TransactionTypeExtractor,
    private val dateExtractor: DateExtractor,
    private val accountExtractor: AccountExtractor
) : SmsParser {

    override fun parse(smsBody: String, smsId: String, sender: String, timestamp: Long): TransactionEntity? {
        val amount = amountExtractor.extract(smsBody) ?: return null
        val type = transactionTypeExtractor.extract(smsBody)
        if (type == "UNKNOWN") return null // Ignore non-transactional messages
        
        val merchant = merchantExtractor.extract(smsBody) ?: sender
        val date = dateExtractor.extract(smsBody) ?: timestamp
        
        val bankName = accountExtractor.extractBankName(smsBody)
        val lastFour = accountExtractor.extractLastFour(smsBody)
        
        // Simple hash to prevent duplicates
        val rawHash = hashString("$sender$smsBody$date")

        return TransactionEntity(
            amountPaise = amount,
            type = type,
            merchant = merchant,
            category = "UNCATEGORIZED",
            transactionDate = date,
            paymentMethod = bankName ?: "UNKNOWN", // Store Bank/Card type here
            accountLastFour = lastFour, 
            referenceNumber = null,
            smsId = smsId,
            rawSmsHash = rawHash,
            confidence = 0.8f,
            source = "SMS",
            isReviewed = false
        )
    }
    
    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
