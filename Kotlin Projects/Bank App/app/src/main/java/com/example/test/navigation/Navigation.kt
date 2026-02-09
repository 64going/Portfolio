package com.example.test.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.test.screens.AddEditTransactionScreen
import com.example.test.screens.CategoryManagementScreen
import com.example.test.screens.HomeScreen
import com.example.test.screens.TransactionListScreen
import com.example.test.viewmodel.FinanceViewModel

@Composable
fun ScreenNavigation(viewModel: FinanceViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(viewModel, onTransactionNav = {navController.navigate("transactionList")}, onCategoryNav = {navController.navigate("categoryManagement")})
        }
        composable("transactionList") {
            TransactionListScreen(viewModel, onAddNav = {navController.navigate("addEditTransactions")}, onBack = {navController.popBackStack()},onEditNav = {edit,transactionId -> navController.navigate("addEditTransactions/$edit/$transactionId")})
        }
        composable("addEditTransactions") {
            AddEditTransactionScreen(viewModel, onBack = {navController.popBackStack()})
        }
        composable("addEditTransactions/{edit}/{transactionId}") {backStackEntry->
            val edit = backStackEntry.arguments?.getString("edit")
            val transactionId = backStackEntry.arguments?.getString("transactionId")
            AddEditTransactionScreen(viewModel, onBack = {navController.popBackStack()}, add = edit.toString(),transactionId = transactionId.toString())
        }
        composable("categoryManagement") {
            CategoryManagementScreen(viewModel, onBack = {navController.popBackStack()})
        }
    }
}