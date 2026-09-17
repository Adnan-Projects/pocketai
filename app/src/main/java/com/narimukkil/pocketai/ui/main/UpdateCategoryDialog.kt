package com.narimukkil.pocketai.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.narimukkil.pocketai.data.local.entity.TransactionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateCategoryDialog(
    transaction: TransactionEntity,
    customCategories: List<String>,
    onDismiss: () -> Unit,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var customCategory by remember { 
        mutableStateOf(if (transaction.category == "UNCATEGORIZED") "" else transaction.category) 
    }
    
    val isEdit = transaction.category != "UNCATEGORIZED"
    
    val baseCategories = listOf(
        "Food & Dining", "Transportation", "Shopping", 
        "Entertainment", "Utilities", "Health", "Education",
        "Mobile & Internet", "General Income", "Salary", "Investment"
    )
    
    val allCategories = (customCategories + baseCategories).distinct().sorted()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(if (isEdit) "Edit Category" else "Categorize ${transaction.merchant ?: "Transaction"}") 
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Select a category or type a new one:")
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = customCategory,
                    onValueChange = { customCategory = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                
                if (customCategory.isEmpty()) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp).padding(top = 8.dp)
                    ) {
                        items(allCategories) { cat ->
                            Text(
                                text = cat,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { customCategory = cat }
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (customCategory.isNotBlank()) {
                        onCategorySelected(customCategory.trim())
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
