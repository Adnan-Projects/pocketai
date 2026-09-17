package com.narimukkil.pocketai.domain.usecase

import com.narimukkil.pocketai.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculateTotalExpenseUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<Long> {
        return repository.getAllTransactionsFlow().map { transactions ->
            transactions.filter { it.type == "EXPENSE" }.sumOf { it.amountPaise }
        }
    }
}
