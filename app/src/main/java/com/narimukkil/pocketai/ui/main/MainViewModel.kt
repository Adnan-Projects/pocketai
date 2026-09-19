package com.narimukkil.pocketai.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.narimukkil.pocketai.data.local.entity.BudgetEntity
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import com.narimukkil.pocketai.domain.usecase.AddManualTransactionUseCase
import com.narimukkil.pocketai.domain.usecase.CalculateTotalExpenseForDateRangeUseCase
import com.narimukkil.pocketai.domain.usecase.GetAllTransactionsUseCase
import com.narimukkil.pocketai.domain.usecase.GetBudgetsUseCase
import com.narimukkil.pocketai.domain.usecase.GetCustomCategoriesUseCase
import com.narimukkil.pocketai.domain.usecase.GetTransactionsByDateRangeUseCase
import com.narimukkil.pocketai.domain.usecase.SaveBudgetUseCase
import com.narimukkil.pocketai.domain.usecase.SyncHistoricalSmsUseCase
import com.narimukkil.pocketai.domain.usecase.UpdateTransactionCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import android.content.Context
import android.os.Environment
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.OutputStreamWriter
import java.io.BufferedWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
import com.narimukkil.pocketai.domain.usecase.ImportStatementUseCase
import kotlinx.coroutines.Dispatchers

data class MainUiState(
    val allTransactions: List<TransactionEntity> = emptyList(),
    val todayTransactions: List<TransactionEntity> = emptyList(),
    val todayTotalPaise: Long = 0L,
    val monthlyTransactions: List<TransactionEntity> = emptyList(),
    val monthlyTotalPaise: Long = 0L,
    val historyDateMillis: Long = 0L,
    val historyTransactions: List<TransactionEntity> = emptyList(),
    val historyTotalPaise: Long = 0L,
    val customCategories: List<String> = emptyList(),
    val budgets: List<BudgetEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MainViewModel @Inject constructor(
    getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase,
    calculateTotalExpenseForDateRangeUseCase: CalculateTotalExpenseForDateRangeUseCase,
    private val syncHistoricalSmsUseCase: SyncHistoricalSmsUseCase,
    private val updateTransactionCategoryUseCase: UpdateTransactionCategoryUseCase,
    private val getCustomCategoriesUseCase: GetCustomCategoriesUseCase,
    private val addManualTransactionUseCase: AddManualTransactionUseCase,
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val saveBudgetUseCase: SaveBudgetUseCase,
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val importStatementUseCase: ImportStatementUseCase
) : ViewModel() {

    private val todayStart = getStartOfDay()
    private val todayEnd = getEndOfDay()
    private val monthStart = getStartOfMonth()
    private val monthEnd = getEndOfMonth()

    private val _historyDateFlow = MutableStateFlow(getStartOfDay())
    private val _customCategoriesFlow = MutableStateFlow<List<String>>(emptyList())

    init {
        fetchCustomCategories()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MainUiState> = combine(
        getTransactionsByDateRangeUseCase(todayStart, todayEnd),
        calculateTotalExpenseForDateRangeUseCase(todayStart, todayEnd),
        getTransactionsByDateRangeUseCase(monthStart, monthEnd),
        calculateTotalExpenseForDateRangeUseCase(monthStart, monthEnd),
        _customCategoriesFlow,
        getBudgetsUseCase(),
        _historyDateFlow.flatMapLatest { d -> getTransactionsByDateRangeUseCase(d, d + 86399999L) },
        _historyDateFlow.flatMapLatest { d -> calculateTotalExpenseForDateRangeUseCase(d, d + 86399999L) },
        _historyDateFlow,
        getAllTransactionsUseCase()
    ) { params ->
        val todayT = params[0] as List<TransactionEntity>
        val todayTotal = params[1] as Long
        val monthT = params[2] as List<TransactionEntity>
        val monthTotal = params[3] as Long
        val customCats = params[4] as List<String>
        val budgetsList = params[5] as List<BudgetEntity>
        val historyT = params[6] as List<TransactionEntity>
        val historyTotal = params[7] as Long
        val historyDate = params[8] as Long
        val allTxns = params[9] as List<TransactionEntity>
        
        MainUiState(
            allTransactions = allTxns,
            todayTransactions = todayT,
            todayTotalPaise = todayTotal,
            monthlyTransactions = monthT,
            monthlyTotalPaise = monthTotal,
            customCategories = customCats,
            budgets = budgetsList,
            historyDateMillis = historyDate,
            historyTransactions = historyT,
            historyTotalPaise = historyTotal,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    fun updateHistoryDate(timeInMillis: Long) {
        val cal = Calendar.getInstance().apply { this.timeInMillis = timeInMillis }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        _historyDateFlow.value = cal.timeInMillis
    }

    fun syncHistoricalData() {
        viewModelScope.launch {
            syncHistoricalSmsUseCase()
        }
    }

    fun updateCategory(transaction: TransactionEntity, newCategory: String) {
        viewModelScope.launch {
            updateTransactionCategoryUseCase(transaction, newCategory)
            fetchCustomCategories() // Refresh the list
        }
    }

    fun addManualTransaction(amountPaise: Long, merchant: String, category: String, type: String) {
        viewModelScope.launch {
            addManualTransactionUseCase(amountPaise, merchant, category, type, System.currentTimeMillis())
            fetchCustomCategories() // Refresh in case it's a new category
        }
    }

    fun saveBudget(category: String, limitPaise: Long) {
        viewModelScope.launch {
            saveBudgetUseCase(category, limitPaise)
        }
    }

    private fun fetchCustomCategories() {
        viewModelScope.launch {
            _customCategoriesFlow.value = getCustomCategoriesUseCase()
        }
    }

    fun exportMonthlyReport(context: Context) {
        viewModelScope.launch {
            val transactions = uiState.value.allTransactions
            if (transactions.isEmpty()) {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "No transactions to export", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            // Group by month string (e.g., "September 2026")
            val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            val groupedByMonth = transactions.groupBy { monthFormat.format(Date(it.transactionDate)) }
            
            try {
                val csvContent = buildString {
                    append("Month,Date,Merchant,Category,Type,Amount (INR),Payment Method\n")
                    
                    for ((monthStr, monthTxns) in groupedByMonth) {
                        for (t in monthTxns) {
                            val dateStr = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date(t.transactionDate))
                            val amount = "%.2f".format(t.amountPaise / 100.0)
                            val type = t.type
                            val merchant = t.merchant?.replace(",", " ") ?: "Unknown"
                            val cat = t.category.replace(",", " ")
                            val method = t.paymentMethod ?: "Unknown"
                            
                            append("$monthStr,$dateStr,$merchant,$cat,$type,$amount,$method\n")
                        }
                    }
                }

                // Save to Downloads folder using MediaStore (Android 10+)
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, "PocketAI_Monthly_Report_${System.currentTimeMillis()}.csv")
                    put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                }

                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                } else {
                    // Fallback for older versions if needed, though scoped storage makes this tricky. 
                    // Assuming API 29+ is preferred target for this feature based on current standards.
                    null
                }
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                            writer.write(csvContent)
                        }
                    }
                    launch(kotlinx.coroutines.Dispatchers.Main) {
                        Toast.makeText(context, "Exported to Downloads successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(kotlinx.coroutines.Dispatchers.Main) {
                    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun importStatement(uri: Uri, context: Context) {
        viewModelScope.launch {
            val result = importStatementUseCase(uri)
            launch(Dispatchers.Main) {
                result.onSuccess { count ->
                    if (count > 0) {
                        Toast.makeText(context, "Successfully imported $count new transactions", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "No new transactions found to import (Duplicates ignored)", Toast.LENGTH_LONG).show()
                    }
                }.onFailure { e ->
                    Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getStartOfDay(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getEndOfDay(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    private fun getStartOfMonth(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getEndOfMonth(): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis // compute
        cal.add(Calendar.MILLISECOND, -1)
        return cal.timeInMillis
    }
}
