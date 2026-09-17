package com.narimukkil.pocketai.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import com.narimukkil.pocketai.domain.usecase.CalculateTotalExpenseForDateRangeUseCase
import com.narimukkil.pocketai.domain.usecase.GetCustomCategoriesUseCase
import com.narimukkil.pocketai.domain.usecase.GetTransactionsByDateRangeUseCase
import com.narimukkil.pocketai.domain.usecase.SyncHistoricalSmsUseCase
import com.narimukkil.pocketai.domain.usecase.UpdateTransactionCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class MainUiState(
    val todayTransactions: List<TransactionEntity> = emptyList(),
    val todayTotalPaise: Long = 0L,
    val monthlyTransactions: List<TransactionEntity> = emptyList(),
    val monthlyTotalPaise: Long = 0L,
    val customCategories: List<String> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MainViewModel @Inject constructor(
    getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase,
    calculateTotalExpenseForDateRangeUseCase: CalculateTotalExpenseForDateRangeUseCase,
    private val syncHistoricalSmsUseCase: SyncHistoricalSmsUseCase,
    private val updateTransactionCategoryUseCase: UpdateTransactionCategoryUseCase,
    private val getCustomCategoriesUseCase: GetCustomCategoriesUseCase
) : ViewModel() {

    private val todayStart = getStartOfDay()
    private val todayEnd = getEndOfDay()
    private val monthStart = getStartOfMonth()
    private val monthEnd = getEndOfMonth()

    private val _customCategoriesFlow = MutableStateFlow<List<String>>(emptyList())

    init {
        fetchCustomCategories()
    }

    val uiState: StateFlow<MainUiState> = combine(
        getTransactionsByDateRangeUseCase(todayStart, todayEnd),
        calculateTotalExpenseForDateRangeUseCase(todayStart, todayEnd),
        getTransactionsByDateRangeUseCase(monthStart, monthEnd),
        calculateTotalExpenseForDateRangeUseCase(monthStart, monthEnd),
        _customCategoriesFlow
    ) { todayT, todayTotal, monthT, monthTotal, customCats ->
        MainUiState(
            todayTransactions = todayT,
            todayTotalPaise = todayTotal,
            monthlyTransactions = monthT,
            monthlyTotalPaise = monthTotal,
            customCategories = customCats,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

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

    private fun fetchCustomCategories() {
        viewModelScope.launch {
            _customCategoriesFlow.value = getCustomCategoriesUseCase()
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
