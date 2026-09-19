package com.narimukkil.pocketai.ui.main.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.narimukkil.pocketai.ui.main.MainUiState
import com.narimukkil.pocketai.ui.main.SummaryCard
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import com.narimukkil.pocketai.ui.main.TransactionItem

@Composable
fun TodayScreen(
    uiState: MainUiState,
    onCategoryClick: (TransactionEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SummaryCard(title = "Today's Expenses", amount = uiState.todayTotalPaise)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Today's Transactions",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.todayTransactions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No transactions found for today.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.todayTransactions,
                    key = { it.id }
                ) { transaction ->
                    Box(modifier = Modifier.animateItem()) {
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
