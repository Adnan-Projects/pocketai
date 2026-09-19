package com.narimukkil.pocketai.domain.usecase

import com.narimukkil.pocketai.data.local.entity.BudgetEntity
import com.narimukkil.pocketai.data.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetsUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(): Flow<List<BudgetEntity>> {
        return budgetRepository.getAllBudgetsFlow()
    }
}
