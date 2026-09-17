package com.narimukkil.pocketai.ui.main.tabs.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import com.narimukkil.pocketai.ui.main.MainUiState

@Composable
fun ReportsScreen(uiState: MainUiState) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Daily", "Monthly")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold) }
                )
            }
        }

        val transactions = if (selectedTab == 0) uiState.todayTransactions else uiState.monthlyTransactions
        val expensesOnly = transactions.filter { it.type == "EXPENSE" }
        val categoryTotals = expensesOnly.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amountPaise } }
            .toList()
            .sortedByDescending { it.second }

        val totalPaise = if (selectedTab == 0) uiState.todayTotalPaise else uiState.monthlyTotalPaise

        if (expensesOnly.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No expenses found for this period.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Total Expenses: ₹${"%.2f".format(totalPaise / 100.0)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(categoryTotals) { (category, totalAmount) ->
                    val percentage = if (totalPaise > 0) (totalAmount.toFloat() / totalPaise) * 100 else 0f
                    CategoryProgressItem(
                        category = category,
                        amountPaise = totalAmount,
                        percentage = percentage
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryProgressItem(category: String, amountPaise: Long, percentage: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "₹${"%.2f".format(amountPaise / 100.0)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${"%.1f".format(percentage)}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
