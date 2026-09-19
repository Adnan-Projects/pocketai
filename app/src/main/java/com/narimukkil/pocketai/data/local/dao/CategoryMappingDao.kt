package com.narimukkil.pocketai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.narimukkil.pocketai.data.local.entity.CategoryMappingEntity

@Dao
interface CategoryMappingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(mapping: CategoryMappingEntity)

    @Query("SELECT category FROM category_mappings WHERE :merchant LIKE '%' || merchantKeyword || '%' OR merchantKeyword LIKE '%' || :merchant || '%' LIMIT 1")
    suspend fun getCategoryForMerchant(merchant: String): String?

    @Query("SELECT DISTINCT category FROM category_mappings")
    suspend fun getAllCustomCategories(): List<String>
}
