package com.narimukkil.pocketai.domain.usecase

import com.narimukkil.pocketai.data.repository.BudgetRepository
import javax.inject.Inject

class SaveBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(category: String, limitPaise: Long) {
        budgetRepository.saveBudget(category, limitPaise)
    }
}
