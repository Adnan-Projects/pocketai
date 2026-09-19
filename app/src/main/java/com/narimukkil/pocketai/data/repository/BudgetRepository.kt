package com.narimukkil.pocketai.data.repository

import com.narimukkil.pocketai.data.local.dao.BudgetDao
import com.narimukkil.pocketai.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao
) {
    suspend fun saveBudget(category: String, limitPaise: Long) {
        budgetDao.insertOrUpdate(BudgetEntity(category, limitPaise))
    }

    fun getAllBudgetsFlow(): Flow<List<BudgetEntity>> {
        return budgetDao.getAllBudgetsFlow()
    }
}
