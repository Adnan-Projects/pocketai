package com.narimukkil.pocketai.ui.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.narimukkil.pocketai.ui.main.tabs.MonthlyScreen
import com.narimukkil.pocketai.ui.main.tabs.TodayScreen
import com.narimukkil.pocketai.ui.main.tabs.reports.ReportsScreen
import com.narimukkil.pocketai.data.local.entity.TransactionEntity
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.ui.platform.LocalContext
import com.narimukkil.pocketai.ui.main.tabs.reports.SetBudgetDialog

import androidx.compose.material.icons.rounded.History

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Daily : BottomNavItem("daily", Icons.Rounded.Home, "Daily")
    object Monthly : BottomNavItem("monthly", Icons.Rounded.DateRange, "Monthly")
    object Reports : BottomNavItem("reports", Icons.Rounded.BarChart, "Reports")
    object History : BottomNavItem("history", Icons.Rounded.History, "History")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    isDarkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {}
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    
    var transactionToCategorize by remember { mutableStateOf<TransactionEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    // Budget State
    var categoryToBudget by remember { mutableStateOf<String?>(null) }
    var currentLimitToBudget by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.syncHistoricalData()
    }

    val items = listOf(
        BottomNavItem.Daily,
        BottomNavItem.Monthly,
        BottomNavItem.History,
        BottomNavItem.Reports
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "PocketAI", 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    val context = LocalContext.current
                    val importLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.OpenDocument(),
                        onResult = { uri ->
                            uri?.let { viewModel.importStatement(it, context) }
                        }
                    )

                    IconButton(onClick = { importLauncher.launch(arrayOf("text/csv", "text/comma-separated-values")) }) {
                        Icon(
                            imageVector = Icons.Rounded.UploadFile,
                            contentDescription = "Import Statement",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = { viewModel.exportMonthlyReport(context) }) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Export Report",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = { viewModel.syncHistoricalData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync Historical SMS",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                item.icon, 
                                contentDescription = item.label,
                                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            ) 
                        },
                        label = { 
                            Text(
                                item.label,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            ) 
                        },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.surface,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        onClick = {
                            navController.navigate(item.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute == BottomNavItem.Daily.route || 
                currentRoute == BottomNavItem.Monthly.route ||
                currentRoute == BottomNavItem.History.route) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Daily.route,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable(BottomNavItem.Daily.route) {
                TodayScreen(uiState) { transaction ->
                    transactionToCategorize = transaction
                }
            }
            composable(BottomNavItem.Monthly.route) {
                MonthlyScreen(uiState) { transaction ->
                    transactionToCategorize = transaction
                }
            }
            composable(BottomNavItem.History.route) {
                com.narimukkil.pocketai.ui.main.tabs.HistoryScreen(
                    uiState = uiState,
                    onCategoryClick = { transaction -> transactionToCategorize = transaction }
                )
            }
            composable(BottomNavItem.Reports.route) {
                ReportsScreen(uiState) { category, currentLimit ->
                    categoryToBudget = category
                    currentLimitToBudget = currentLimit
                }
            }
        }

        transactionToCategorize?.let { transaction ->
            UpdateCategoryDialog(
                transaction = transaction,
                customCategories = uiState.customCategories,
                onDismiss = { transactionToCategorize = null },
                onCategorySelected = { newCategory ->
                    viewModel.updateCategory(transaction, newCategory)
                    transactionToCategorize = null
                }
            )
        }

        if (showAddDialog) {
            AddTransactionDialog(
                customCategories = uiState.customCategories,
                onDismiss = { showAddDialog = false },
                onSave = { amountPaise, merchant, category, type ->
                    viewModel.addManualTransaction(amountPaise, merchant, category, type)
                    showAddDialog = false
                }
            )
        }

        categoryToBudget?.let { category ->
            SetBudgetDialog(
                category = category,
                currentLimitPaise = currentLimitToBudget,
                onDismiss = { categoryToBudget = null },
                onSave = { limitPaise ->
                    viewModel.saveBudget(category, limitPaise)
                    categoryToBudget = null
                }
            )
        }
    }
}
