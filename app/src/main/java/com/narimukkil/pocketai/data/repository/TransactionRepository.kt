package com.narimukkil.pocketai.data.repository

import com.narimukkil.pocketai.data.local.dao.TransactionDao
import com.narimukkil.pocketai.data.local.entity.TransactionEntity

class TransactionRepository(
    private val transactionDao: TransactionDao
) {

    suspend fun insert(transaction: TransactionEntity): Long {
        return transactionDao.insert(transaction)
    }

    suspend fun update(transaction: TransactionEntity) {
        transactionDao.update(transaction)
    }

    suspend fun delete(transaction: TransactionEntity) {
        transactionDao.delete(transaction)
    }

    suspend fun getAllTransactions(): List<TransactionEntity> {
        return transactionDao.getAllTransactions()
    }

    suspend fun getTransactionsBetween(
        startDate: Long,
        endDate: Long
    ): List<TransactionEntity> {
        return transactionDao.getTransactionsBetween(startDate, endDate)
    }

    suspend fun getTransactionsByCategory(
        category: String
    ): List<TransactionEntity> {
        return transactionDao.getTransactionsByCategory(category)
    }

    suspend fun findByReferenceNumber(
        referenceNumber: String
    ): TransactionEntity? {
        return transactionDao.findByReferenceNumber(referenceNumber)
    }

    suspend fun findBySmsHash(
        smsHash: String
    ): TransactionEntity? {
        return transactionDao.findBySmsHash(smsHash)
    }
}