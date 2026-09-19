package com.narimukkil.pocketai.ui.main.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import com.narimukkil.pocketai.ui.main.MainUiState
import com.narimukkil.pocketai.ui.main.TransactionItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    uiState: MainUiState,
    onCategoryClick: (TransactionEntity) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    
    // Group transactions by date string. Since they are already ordered descending by the DAO, 
    // the map will retain the descending order of days.
    val groupedTransactions = remember(uiState.allTransactions) {
        uiState.allTransactions.groupBy { dateFormat.format(Date(it.transactionDate)) }
    }

    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Transaction History",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(16.dp)
        )

        if (groupedTransactions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No transactions found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                groupedTransactions.forEach { (dateStr, txns) ->
                    item(key = "header_$dateStr") {
                        val isExpanded = expandedStates[dateStr] ?: false
                        val totalExpense = txns.filter { it.type == "EXPENSE" }.sumOf { it.amountPaise }
                        
                        DailyHeader(
                            dateString = dateStr,
                            totalExpensePaise = totalExpense,
                            isExpanded = isExpanded,
                            onClick = { expandedStates[dateStr] = !isExpanded }
                        )
                    }
                    
                    if (expandedStates[dateStr] == true) {
                        items(
                            items = txns,
                            key = { it.id }
                        ) { transaction ->
                            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                TransactionItem(
                                    transaction = transaction,
                                    onCategoryClick = onCategoryClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyHeader(
    dateString: String,
    totalExpensePaise: Long,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (totalExpensePaise > 0) {
                    Text(
                        text = "Spent: ₹${"%.2f".format(totalExpensePaise / 100.0)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "No expenses",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
