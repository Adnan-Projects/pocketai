package com.narimukkil.pocketai.ml

import com.narimukkil.pocketai.data.local.dao.CategoryMappingDao
import javax.inject.Inject

class CategoryClassifier @Inject constructor(
    private val categoryMappingDao: CategoryMappingDao
) {
    suspend fun categorize(merchant: String, type: String): String {
        // 1. Check user-defined mappings (The Learning Model)
        val userCategory = categoryMappingDao.getCategoryForMerchant(merchant)
        if (userCategory != null) {
            return userCategory
        }

        // 2. Pre-trained heuristic fallbacks
        val lowerMerchant = merchant.lowercase()
        
        if (type == "INCOME") {
            if (lowerMerchant.contains("salary") || lowerMerchant.contains("employer")) return "Salary"
            if (lowerMerchant.contains("interest") || lowerMerchant.contains("dividend")) return "Investment"
            if (lowerMerchant.contains("refund")) return "Refunds"
            return "General Income"
        }

        // Common Expense Categories
        return when {
            lowerMerchant.contains("swiggy") || lowerMerchant.contains("zomato") || 
            lowerMerchant.contains("starbucks") || lowerMerchant.contains("restaurant") ||
            lowerMerchant.contains("kfc") || lowerMerchant.contains("mcdonald") -> "Food & Dining"
            
            lowerMerchant.contains("uber") || lowerMerchant.contains("ola") || 
            lowerMerchant.contains("petrol") || lowerMerchant.contains("fuel") ||
            lowerMerchant.contains("irctc") || lowerMerchant.contains("redbus") -> "Transportation"
            
            lowerMerchant.contains("amazon") || lowerMerchant.contains("flipkart") || 
            lowerMerchant.contains("myntra") || lowerMerchant.contains("shopping") -> "Shopping"
            
            lowerMerchant.contains("netflix") || lowerMerchant.contains("spotify") || 
            lowerMerchant.contains("prime") || lowerMerchant.contains("hotstar") -> "Entertainment"
            
            lowerMerchant.contains("jio") || lowerMerchant.contains("airtel") || 
            lowerMerchant.contains("vi") || lowerMerchant.contains("bsnl") ||
            lowerMerchant.contains("recharge") -> "Mobile & Internet"
            
            lowerMerchant.contains("bill") || lowerMerchant.contains("electricity") || 
            lowerMerchant.contains("water") || lowerMerchant.contains("gas") -> "Utilities"
            
            lowerMerchant.contains("medical") || lowerMerchant.contains("pharmacy") || 
            lowerMerchant.contains("apollo") || lowerMerchant.contains("hospital") -> "Health"
            
            else -> "UNCATEGORIZED"
        }
    }
}
