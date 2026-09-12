package com.narimukkil.pocketai.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Money is stored in paise, not Double/Float
    val amountPaise: Long,

    // EXPENSE or INCOME
    val type: String,

    // SWIGGY, AMAZON, UBER, etc.
    val merchant: String?,

    // FOOD, SHOPPING, TRANSPORT, etc.
    val category: String,

    // When the transaction happened
    val transactionDate: Long,

    // UPI, CARD, ATM, NEFT, IMPS, etc.
    val paymentMethod: String?,

    // Last 4 digits if available
    val accountLastFour: String?,

    // Bank/UPI reference number
    val referenceNumber: String?,

    // Original SMS identifier when available
    val smsId: String?,

    // Hash of SMS instead of storing financial SMS unnecessarily
    val rawSmsHash: String?,

    // AI confidence: 0.0 - 1.0
    val confidence: Float?,

    // SMS, MANUAL, etc.
    val source: String,

    // Whether the user has reviewed/corrected it
    val isReviewed: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)