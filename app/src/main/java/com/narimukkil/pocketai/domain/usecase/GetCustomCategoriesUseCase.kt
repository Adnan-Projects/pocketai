package com.narimukkil.pocketai.domain.usecase

import com.narimukkil.pocketai.data.local.dao.CategoryMappingDao
import javax.inject.Inject

class GetCustomCategoriesUseCase @Inject constructor(
    private val categoryMappingDao: CategoryMappingDao
) {
    suspend operator fun invoke(): List<String> {
        return categoryMappingDao.getAllCustomCategories()
    }
}
