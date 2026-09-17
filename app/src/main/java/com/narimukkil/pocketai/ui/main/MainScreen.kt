package com.narimukkil.pocketai.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Home
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

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Today : BottomNavItem("today", Icons.Rounded.Home, "Today")
    object Monthly : BottomNavItem("monthly", Icons.Rounded.DateRange, "Monthly")
    object Reports : BottomNavItem("reports", Icons.Rounded.BarChart, "Reports")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    
    var transactionToCategorize by remember { mutableStateOf<TransactionEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.syncHistoricalData()
    }

    val items = listOf(
        BottomNavItem.Today,
        BottomNavItem.Monthly,
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Today.route,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable(BottomNavItem.Today.route) {
                TodayScreen(uiState) { transaction ->
                    transactionToCategorize = transaction
                }
            }
            composable(BottomNavItem.Monthly.route) {
                MonthlyScreen(uiState) { transaction ->
                    transactionToCategorize = transaction
                }
            }
            composable(BottomNavItem.Reports.route) {
                ReportsScreen(uiState)
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
    }
}
