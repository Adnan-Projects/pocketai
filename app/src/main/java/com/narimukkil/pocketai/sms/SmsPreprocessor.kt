package com.narimukkil.pocketai.sms

class SmsPreprocessor {

    fun preprocess(text: String): String {
        return text
            .replace("\u00A0", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .uppercase()
    }

    fun looksLikeTransaction(text: String): Boolean {

        val transactionKeywords = listOf(
            "DEBITED",
            "DEBIT",
            "SPENT",
            "PAID",
            "PURCHASE",
            "WITHDRAWN",
            "SENT",
            "TRANSFERRED",
            "PAYMENT SUCCESSFUL",
            "CREDITED",
            "CREDIT",
            "RECEIVED",
            "DEPOSITED",
            "SALARY",
            "REFUND",
            "CASHBACK"
        )

        val transactionIndicators = listOf(
            "UPI",
            "NEFT",
            "IMPS",
            "ATM",
            "POS",
            "CARD",
            "A/C",
            "ACCOUNT",
            "TRANSACTION",
            "REFERENCE"
        )

        val keywordMatch = transactionKeywords.any { keyword ->
            text.contains(keyword)
        }

        val indicatorMatch = transactionIndicators.any { indicator ->
            text.contains(indicator)
        }

        return keywordMatch && indicatorMatch
    }
}