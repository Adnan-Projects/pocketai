package com.narimukkil.pocketai.domain.usecase

import com.narimukkil.pocketai.data.local.dao.CategoryMappingDao
import com.narimukkil.pocketai.data.local.entity.CategoryMappingEntity
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import com.narimukkil.pocketai.data.repository.TransactionRepository
import javax.inject.Inject

class AddManualTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryMappingDao: CategoryMappingDao
) {
    suspend operator fun invoke(amountPaise: Long, merchant: String, category: String, type: String, date: Long) {
        val transaction = TransactionEntity(
            amountPaise = amountPaise,
            type = type,
            merchant = merchant.ifBlank { "Cash" },
            category = category,
            transactionDate = date,
            paymentMethod = "CASH",
            accountLastFour = null,
            referenceNumber = null,
            smsId = null,
            rawSmsHash = null,
            confidence = 1.0f,
            source = "MANUAL",
            isReviewed = true
        )
        transactionRepository.insert(transaction)

        if (merchant.isNotBlank()) {
            categoryMappingDao.insertOrUpdate(CategoryMappingEntity(merchant, category))
        }
    }
}
