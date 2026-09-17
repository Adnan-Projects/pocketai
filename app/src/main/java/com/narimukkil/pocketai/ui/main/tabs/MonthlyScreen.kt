package com.narimukkil.pocketai.ui.main.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.narimukkil.pocketai.ui.main.MainUiState
import com.narimukkil.pocketai.ui.main.SummaryCard
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import com.narimukkil.pocketai.ui.main.TransactionItem

@Composable
fun MonthlyScreen(
    uiState: MainUiState,
    onCategoryClick: (TransactionEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SummaryCard(title = "This Month's Expenses", amount = uiState.monthlyTotalPaise)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Monthly Transactions",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.monthlyTransactions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No transactions found for this month.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.monthlyTransactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onCategoryClick = onCategoryClick
                    )
                }
            }
        }
    }
}
