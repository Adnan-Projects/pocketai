package com.narimukkil.pocketai.domain.usecase

import com.narimukkil.pocketai.data.local.dao.CategoryMappingDao
import com.narimukkil.pocketai.data.local.entity.CategoryMappingEntity
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import com.narimukkil.pocketai.data.repository.TransactionRepository
import javax.inject.Inject

class UpdateTransactionCategoryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryMappingDao: CategoryMappingDao
) {
    suspend operator fun invoke(transaction: TransactionEntity, newCategory: String) {
        // 1. Update the transaction
        val updatedTransaction = transaction.copy(category = newCategory)
        transactionRepository.update(updatedTransaction)

        // 2. Save the mapping so the ML model learns
        val merchant = transaction.merchant
        if (!merchant.isNullOrBlank()) {
            val mapping = CategoryMappingEntity(
                merchantKeyword = merchant,
                category = newCategory
            )
            categoryMappingDao.insertOrUpdate(mapping)
        }
    }
}
