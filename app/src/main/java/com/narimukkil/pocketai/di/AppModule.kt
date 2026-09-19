package com.narimukkil.pocketai.di

import android.content.Context
import com.narimukkil.pocketai.data.local.AppDatabase
import com.narimukkil.pocketai.data.local.dao.BudgetDao
import com.narimukkil.pocketai.data.local.dao.CategoryMappingDao
import com.narimukkil.pocketai.data.local.dao.TransactionDao
import com.narimukkil.pocketai.data.repository.TransactionRepository
import com.narimukkil.pocketai.parser.RegexSmsParserImpl
import com.narimukkil.pocketai.parser.SmsParser
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTransactionDao(appDatabase: AppDatabase): TransactionDao {
        return appDatabase.transactionDao()
    }

    @Provides
    @Singleton
    fun provideCategoryMappingDao(appDatabase: AppDatabase): CategoryMappingDao {
        return appDatabase.categoryMappingDao()
    }

    @Provides
    @Singleton
    fun provideBudgetDao(appDatabase: AppDatabase): BudgetDao {
        return appDatabase.budgetDao()
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(transactionDao: TransactionDao): TransactionRepository {
        return TransactionRepository(transactionDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ParserModule {

    @Binds
    @Singleton
    abstract fun bindSmsParser(
        regexSmsParserImpl: RegexSmsParserImpl
    ): SmsParser
}
