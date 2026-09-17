package com.narimukkil.pocketai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.narimukkil.pocketai.data.local.dao.CategoryMappingDao
import com.narimukkil.pocketai.data.local.dao.TransactionDao
import com.narimukkil.pocketai.data.local.entity.CategoryMappingEntity
import com.narimukkil.pocketai.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, CategoryMappingEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryMappingDao(): CategoryMappingDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pocketai_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}