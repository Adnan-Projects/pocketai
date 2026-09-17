package com.narimukkil.pocketai.domain.usecase

import com.narimukkil.pocketai.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculateTotalExpenseForDateRangeUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<Long> {
        return repository.getTransactionsBetweenFlow(startDate, endDate).map { transactions ->
            transactions.filter { it.type == "EXPENSE" }.sumOf { it.amountPaise }
        }
    }
}
