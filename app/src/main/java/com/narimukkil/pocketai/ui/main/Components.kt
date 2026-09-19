package com.narimukkil.pocketai.ui.main

import androidx.compose.foundation.background
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SummaryCard(title: String, amount: Long) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₹${"%.2f".format(amount / 100.0)}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    onCategoryClick: ((TransactionEntity) -> Unit)? = null
) {
    val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(transaction.transactionDate))
    val isExpense = transaction.type == "EXPENSE"
    val isUncategorized = transaction.category == "UNCATEGORIZED"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Initials circle
        val merchantName = transaction.merchant ?: "Unknown"
        val initial = merchantName.take(1).uppercase()
        
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isExpense) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else Color(0xFF34C759).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = if (isExpense) MaterialTheme.colorScheme.error else Color(0xFF34C759),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = merchantName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            val categoryText = if (isUncategorized) "Add Category" else transaction.category
            val categoryColor = if (isUncategorized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$dateString • ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = categoryText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = categoryColor,
                    modifier = Modifier.clickable(enabled = onCategoryClick != null) {
                        onCategoryClick?.invoke(transaction)
                    }
                )
            }
        }

        // Amount
        Text(
            text = "${if (isExpense) "-" else "+"}₹${"%.2f".format(transaction.amountPaise / 100.0)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isExpense) MaterialTheme.colorScheme.error else Color(0xFF34C759)
        )
    }
}
