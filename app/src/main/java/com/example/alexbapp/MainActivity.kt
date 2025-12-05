package com.example.alexbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alexbapp.ui.screens.AddExpenseScreen
import com.example.alexbapp.ui.screens.ExpenseChartScreen
import com.example.alexbapp.ui.screens.ExpenseListScreen
import com.example.alexbapp.ui.screens.ManageCategoriesScreen
import com.example.alexbapp.ui.screens.NotificationSettingsScreen
import com.example.alexbapp.ui.screens.PeriodSelectionScreen
import com.example.alexbapp.ui.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerApp()
        }
    }
}

@Composable
fun ExpenseTrackerApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            val viewModel: ExpenseViewModel = viewModel()
            
            NavHost(
                navController = navController,
                startDestination = "expense_list"
            ) {
                composable("expense_list") {
                    // Restore all expenses when returning to main screen
                    LaunchedEffect(Unit) {
                        viewModel.loadAllExpenses()
                        viewModel.loadExpenses()
                    }
                    ExpenseListScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
                composable("add_expense") {
                    AddExpenseScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
                composable("manage_categories") {
                    ManageCategoriesScreen(
                        navController = navController,
                        onAddCategory = { category -> viewModel.addCustomCategory(category) },
                        onDeleteCategory = { category -> viewModel.removeCustomCategory(category) },
                        customCategories = viewModel.customCategories
                    )
                }
                composable("select_period") {
                    PeriodSelectionScreen(
                        navController = navController
                    )
                }
                composable("notification_settings") {
                    NotificationSettingsScreen(
                        navController = navController
                    )
                }
                composable(
                    "expense_chart/{startDate}/{endDate}",
                    arguments = listOf(
                        navArgument("startDate") { defaultValue = "" },
                        navArgument("endDate") { defaultValue = "" }
                    )
                ) { backStackEntry ->
                    val startDate = backStackEntry.arguments?.getString("startDate") ?: ""
                    val endDate = backStackEntry.arguments?.getString("endDate") ?: ""
                    
                    // Load expenses for the selected period
                    LaunchedEffect(startDate, endDate) {
                        viewModel.loadExpensesByDateRange(startDate, endDate)
                    }
                    
                    ExpenseChartScreen(
                        navController = navController,
                        viewModel = viewModel,
                        startDate = startDate,
                        endDate = endDate
                    )
                }
            }
        }
    }
}
