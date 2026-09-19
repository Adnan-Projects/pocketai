package com.narimukkil.pocketai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.narimukkil.pocketai.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(budget: BudgetEntity)

    @Query("SELECT * FROM budgets")
    fun getAllBudgetsFlow(): Flow<List<BudgetEntity>>
}
