package com.narimukkil.pocketai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>
    
    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions
        WHERE transactionDate BETWEEN :startDate AND :endDate
        ORDER BY transactionDate DESC
    """)
    suspend fun getTransactionsBetween(
        startDate: Long,
        endDate: Long
    ): List<TransactionEntity>

    @Query("""
        SELECT * FROM transactions
        WHERE transactionDate BETWEEN :startDate AND :endDate
        ORDER BY transactionDate DESC
    """)
    fun getTransactionsBetweenFlow(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions
        WHERE category = :category
        ORDER BY transactionDate DESC
    """)
    suspend fun getTransactionsByCategory(
        category: String
    ): List<TransactionEntity>

    @Query("""
        SELECT * FROM transactions
        WHERE referenceNumber = :referenceNumber
        LIMIT 1
    """)
    suspend fun findByReferenceNumber(
        referenceNumber: String
    ): TransactionEntity?

    @Query("""
        SELECT * FROM transactions
        WHERE rawSmsHash = :smsHash
        LIMIT 1
    """)
    suspend fun findBySmsHash(
        smsHash: String
    ): TransactionEntity?
}