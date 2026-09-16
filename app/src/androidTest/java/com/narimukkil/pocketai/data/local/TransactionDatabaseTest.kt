package com.narimukkil.pocketai.data.local


import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionDatabaseTest {

    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java,
        ).build()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertAndReadTransaction() = runBlocking {

        val transaction = TransactionEntity(
            amountPaise = 45000L,
            type = "EXPENSE",
            merchant = "SWIGGY",
            category = "FOOD",
            transactionDate = System.currentTimeMillis(),
            paymentMethod = "UPI",
            accountLastFour = null,
            referenceNumber = "TEST123456",
            smsId = "TEST_SMS_001",
            rawSmsHash = "test_hash",
            confidence = 0.95f,
            source = "TEST",
            isReviewed = false,
        )

        val insertedId = database.transactionDao().insert(transaction)

        assertNotNull(insertedId)

        val transactions = database.transactionDao().getAllTransactions()

        assertEquals(1, transactions.size)
        assertEquals(45000L, transactions[0].amountPaise)
        assertEquals("SWIGGY", transactions[0].merchant)
        assertEquals("FOOD", transactions[0].category)
        assertEquals("UPI", transactions[0].paymentMethod)
    }
}