package com.narimukkil.pocketai.ui.main.tabs.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetBudgetDialog(
    category: String,
    currentLimitPaise: Long?,
    onDismiss: () -> Unit,
    onSave: (Long) -> Unit
) {
    var limitStr by remember { 
        mutableStateOf(if (currentLimitPaise != null && currentLimitPaise > 0) "%.2f".format(currentLimitPaise / 100f) else "") 
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Budget for $category") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Enter the maximum monthly amount you want to spend on $category.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = limitStr,
                    onValueChange = { limitStr = it },
                    label = { Text("Budget Limit (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val limitFloat = limitStr.toFloatOrNull()
                    if (limitFloat != null && limitFloat >= 0) {
                        onSave((limitFloat * 100).toLong())
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
