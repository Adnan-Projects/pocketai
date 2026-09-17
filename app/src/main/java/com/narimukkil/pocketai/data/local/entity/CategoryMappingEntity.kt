package com.narimukkil.pocketai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_mappings")
data class CategoryMappingEntity(
    @PrimaryKey
    val merchantKeyword: String,
    val category: String
)
