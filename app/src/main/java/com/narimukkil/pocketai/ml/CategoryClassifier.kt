package com.narimukkil.pocketai.ml

import com.narimukkil.pocketai.data.local.dao.CategoryMappingDao
import javax.inject.Inject

class CategoryClassifier @Inject constructor(
    private val categoryMappingDao: CategoryMappingDao
) {
    // 1. Comprehensive mapping dictionaries for Indian context
    private val expenseCategories = mapOf(
        "Food & Dining" to listOf("swiggy", "zomato", "starbucks", "restaurant", "kfc", "mcdonald", "dominos", "pizza", "cafe", "bakery", "dairy", "eatery", "hotel", "food"),
        "Groceries" to listOf("blinkit", "zepto", "instamart", "bigbasket", "dmart", "reliance smart", "jiomart", "supermarket", "provision", "grocery", "store", "market", "lulu"),
        "Transportation" to listOf("uber", "ola", "rapido", "namma yatri", "irctc", "redbus", "makemytrip", "indigo", "metro", "toll", "fastag", "fuel", "petrol", "diesel", "shell", "hpcl", "bpcl", "indian oil", "auto", "cab", "parking"),
        "Shopping" to listOf("amazon", "flipkart", "myntra", "ajio", "nykaa", "meesho", "shopping", "mall", "apparel", "clothing", "lifestyle", "store", "ikea", "decathlon"),
        "Entertainment" to listOf("netflix", "prime", "hotstar", "spotify", "youtube", "bookmyshow", "pvr", "inox", "cinema", "gaming", "steam", "sony liv", "zee5", "music"),
        "Mobile & Internet" to listOf("jio", "airtel", "vi", "bsnl", "vodafone", "broadband", "recharge", "wifi", "telecom"),
        "Utilities" to listOf("electricity", "water", "gas", "bescom", "kseb", "tata power", "adani", "mahanagar gas", "igl", "bill"),
        "Health & Wellness" to listOf("pharmacy", "apollo", "netmeds", "1mg", "pharmeasy", "hospital", "clinic", "diagnostics", "gym", "cultfit", "health", "fitness", "medical", "doctor"),
        "Finance & Taxes" to listOf("lic", "insurance", "mutual fund", "zerodha", "groww", "upstox", "emi", "loan", "tax", "itd", "policybazaar", "sbi life", "hdfc life")
    )

    private val incomeCategories = mapOf(
        "Salary" to listOf("salary", "employer", "payroll", "tech", "ltd", "pvt", "solutions"),
        "Investment" to listOf("interest", "dividend", "mutual fund", "zerodha", "groww", "upstox"),
        "Refunds" to listOf("refund", "cashback", "reversal", "returned")
    )

    suspend fun categorize(merchant: String, type: String): String {
        // 2. Check user-defined mappings first (The Learning Model)
        // If the user has explicitly corrected this merchant before, their rule ALWAYS wins.
        val userCategory = categoryMappingDao.getCategoryForMerchant(merchant)
        if (userCategory != null) {
            return userCategory
        }

        val lowerMerchant = merchant.lowercase()

        // 3. Classify Income transactions
        if (type == "INCOME") {
            for ((category, keywords) in incomeCategories) {
                if (keywords.any { lowerMerchant.contains(it) }) {
                    return category
                }
            }
            return "General Income"
        }

        // 4. Classify Expense transactions using comprehensive heuristics
        for ((category, keywords) in expenseCategories) {
            if (keywords.any { keyword -> 
                // Regex word boundary ensures we don't accidentally match substrings 
                // e.g., "gas" won't match "gastric", but we also check simple contains for compound words like "AmazonPay"
                Regex("\\b$keyword\\b").containsMatchIn(lowerMerchant) || lowerMerchant.contains(keyword) 
            }) {
                return category
            }
        }

        // 5. Fallback for completely unknown merchants
        return "UNCATEGORIZED"
    }
}
