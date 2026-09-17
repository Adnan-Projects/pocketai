package com.narimukkil.pocketai.domain.usecase

import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import com.narimukkil.pocketai.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<TransactionEntity>> {
        return repository.getAllTransactionsFlow()
    }
}
