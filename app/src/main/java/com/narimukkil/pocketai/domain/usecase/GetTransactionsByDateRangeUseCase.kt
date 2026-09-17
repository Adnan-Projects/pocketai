package com.narimukkil.pocketai.domain.usecase

import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import com.narimukkil.pocketai.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsByDateRangeUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<List<TransactionEntity>> {
        return repository.getTransactionsBetweenFlow(startDate, endDate)
    }
}
