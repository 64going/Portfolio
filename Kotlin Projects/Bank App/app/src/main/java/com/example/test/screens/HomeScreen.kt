package com.example.test.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.test.data.TransactionType
import com.example.test.viewmodel.FinanceViewModel
import java.text.NumberFormat

/*
1. Why is FinanceRepository used instead of calling DAOs directly from the ViewModel? What problem does this solve?
2. Why is FinanceViewModelFactory needed? Why can't FinanceViewModel be instantiated directly in MainActivity?
3. In TransactionListScreen, how does the filter dialog update the displayed transactions? Trace the data flow from user selection to UI update.
4. When navigating from Home → TransactionList → AddEditTransaction, what happens to the back stack? How does popBackStack() work?
5. Why are repository operations wrapped in viewModelScope.launch? What would happen if they were called directly without a coroutine scope?
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: FinanceViewModel,onTransactionNav: () -> Unit,onCategoryNav: () -> Unit){

    var income: Double? by remember { mutableStateOf(0.00) }
    var expinces: Double? by remember { mutableStateOf(0.00) }
    var total:  Double by remember { mutableStateOf(0.00) }
    var isBalancePositive by remember { mutableStateOf(false) }
    val transactions by viewModel.transactions.collectAsState()
    var startDate: Long by remember { mutableStateOf(0) }
    var endDate: Long by remember { mutableStateOf(0) }
    val displayTransactions = transactions.take(5)
    isBalancePositive = total >= 0
    Scaffold (topBar = {TopAppBar(
        title = {Text("Finance Tracker")},
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer, titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer)
    )}){padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if(isBalancePositive){MaterialTheme.colorScheme.primaryContainer}else{MaterialTheme.colorScheme.errorContainer})
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(if(isBalancePositive){Icons.Filled.CheckCircle}else{Icons.Filled.Warning}, contentDescription = "Balance State")
                    Text("Current Balance", style = MaterialTheme.typography.bodyMedium)
                    Text(if(isBalancePositive){
                        "$${formatDouble(total,2)}"
                    }else{"-$${formatDouble(total*-1,2)}"}, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                }
            }
            Row (
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
                ) {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xE62BBD2B))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text("Income", style = MaterialTheme.typography.bodyMedium)
                        Text(if(income != null){"$${formatDouble(income!!,2)}"}else{"null"}, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xE6F57369))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text("Expense", style = MaterialTheme.typography.bodyMedium)
                        Text(if(expinces != null){"-$${formatDouble(expinces!!,2)}"}else{"null"}, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text("Quick Actions",style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row (
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f).clickable{onTransactionNav()},
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Box(modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        Text("Transactions", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f).clickable{onCategoryNav()},
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Box(modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        Text("Categories", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }
            Row (
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Transactions (most recent 5)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton (onClick = {onTransactionNav()}){Text("View All", style = MaterialTheme.typography.bodyMedium) }
            }
            LazyColumn(modifier = Modifier.fillMaxHeight().padding(8.dp)) {
                items(displayTransactions.size) { item ->
                    val transactionItem = transactions[item]

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column() {
                                Text(
                                    "${transactionItem.description}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "${transactionItem.date.toFormattedDate()}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                text = if(transactionItem.type == TransactionType.INCOME){"$${formatDouble(transactionItem.amount,2)}"}else{"-$${formatDouble(transactionItem.amount,2)}"},
                                color = if (transactionItem.type == TransactionType.INCOME) {
                                    Color(0xFF4CAF50)
                                } else {
                                    Color(0xFFF44336)
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun getDateEnd(){
        for (transaction in transactions) {
            if(startDate>transaction.date){
                startDate == transaction.date + startDate
            }
            if (endDate<transaction.date){
                endDate = transaction.date + endDate
            }

        }
        LaunchedEffect(startDate, endDate, TransactionType.INCOME) {
            viewModel.getTotalByTypeAndDateRange(TransactionType.INCOME, startDate, endDate) { result ->
                income = result
            }
        }
        LaunchedEffect(startDate, endDate, TransactionType.EXPENSE) {
            viewModel.getTotalByTypeAndDateRange(TransactionType.EXPENSE, startDate, endDate) { result ->
                expinces = result
            }
        }
        if (income != null && expinces != null) {
            total = income!!-expinces!!
        }else if (expinces != null){
            total = -expinces!!
        } else if(income != null) {
            total = income!!
        }




    }
    getDateEnd()
}
fun formatDouble(value: Double, maxFractionDigits: Int): String {
    val format = NumberFormat.getInstance()
    format.maximumFractionDigits = maxFractionDigits
    format.minimumFractionDigits = maxFractionDigits // Ensure trailing zeros if needed
    return format.format(value)
}