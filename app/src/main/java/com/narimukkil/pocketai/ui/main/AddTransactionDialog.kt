package com.narimukkil.pocketai.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    customCategories: List<String>,
    onDismiss: () -> Unit,
    onSave: (amountPaise: Long, merchant: String, category: String, type: String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var merchant by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val baseCategories = listOf(
        "Food & Dining", "Transportation", "Shopping",
        "Entertainment", "Utilities", "Health", "Education",
        "Mobile & Internet", "General Income", "Salary", "Investment"
    )
    val allCategories = (customCategories + baseCategories).distinct().sorted()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Manual Transaction") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilterChip(
                        selected = isExpense,
                        onClick = { isExpense = true },
                        label = { Text("Expense") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    FilterChip(
                        selected = !isExpense,
                        onClick = { isExpense = false },
                        label = { Text("Income") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = merchant,
                    onValueChange = { merchant = it },
                    label = { Text("Merchant / Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                if (category.isEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 120.dp)
                            .padding(top = 8.dp)
                    ) {
                        items(allCategories) { cat ->
                            Text(
                                text = cat,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { category = cat }
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
                    val amtFloat = amountStr.toFloatOrNull()
                    if (amtFloat != null && amtFloat > 0 && category.isNotBlank()) {
                        val amtPaise = (amtFloat * 100).toLong()
                        val type = if (isExpense) "EXPENSE" else "INCOME"
                        onSave(amtPaise, merchant.trim(), category.trim(), type)
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
