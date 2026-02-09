package com.example.test.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.test.data.Category
import com.example.test.data.Transaction
import com.example.test.data.TransactionType
import com.example.test.viewmodel.FinanceViewModel
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionListScreen(viewModel: FinanceViewModel,onAddNav: () -> Unit, onBack:() -> Unit,onEditNav: (String, String) -> Unit){
    val transactions by viewModel.transactions.collectAsState()
    val categorys by viewModel.categorys.collectAsState()
    var filterDialog by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }
    var sortingBy by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }

    val transactionsByCategory by remember(selectedCategoryId) {
        if (selectedCategoryId == null) {
            flowOf(emptyList())
        } else {
            viewModel.getTransactionsByCategory(selectedCategoryId!!)
        }
    }.collectAsState(initial = emptyList())

    Scaffold (topBar = {TopAppBar(
        title = {Text("Transactions")},
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer, titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer),
        navigationIcon = {IconButton(onClick = {onBack()}){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        } },
        actions = {
            TextButton(onClick = {filterDialog = true}){
                Text("Filter")
            }
            IconButton(onClick = {onAddNav()}){
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        }
    )}){ padding ->

        LazyColumn(modifier = Modifier.fillMaxHeight().padding(padding).padding(8.dp)) {
            items(if(sortingBy){transactionsByCategory.size}else{transactions.size}) { item ->
                val transactionItem = if(sortingBy){transactionsByCategory[item]}else{transactions[item]}

                val category by produceState<Category?>(initialValue = null, key1 = transactionItem.categoryId) {
                    value = viewModel.getCategoryById(transactionItem.categoryId)
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    modifier = Modifier.clickable{onEditNav("Edit",transactionItem.id.toString())}
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column() {
                            Text(
                                transactionItem.description,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    ) {
                                        append(category?.name)
                                    }
                                    append(" • ${transactionItem.date.toFormattedDate()}") },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Row (verticalAlignment = Alignment.CenterVertically){
                        Text(
                            text = if(transactionItem.type== TransactionType.EXPENSE){"-$${formatDouble(transactionItem.amount,2)}"}else{"$${formatDouble(transactionItem.amount,2)}"} ,
                            color = if (transactionItem.type == TransactionType.INCOME) {
                                Color(0xFF4CAF50)
                            } else {
                                Color(0xFFF44336)
                            },
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = {transactionToDelete = transactionItem}) {
                            Icon(imageVector = Icons.Filled.Delete,
                                contentDescription = "delete",
                                tint = MaterialTheme.colorScheme.error
                                )
                        }}
                    }
                }

            }
        }
        transactionToDelete?.let { transaction ->
            AlertDialog(
                onDismissRequest = { transactionToDelete = null },
                title = { Text("Delete ${transaction.description}") },
                text = { Text("Are you sure you want to delete this transaction?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteTransactionById(transaction.id)
                            transactionToDelete = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { transactionToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
    if (filterDialog) {
        Dialog(onDismissRequest = { filterDialog = false }) {
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(24.dp)
                    .fillMaxWidth(.8f)
            ) {
                Column {
                    Text("Filter by Category", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = {sortingBy = false}) { Text("All Categories") }
                    FlowColumn {
                        categorys.forEach { item ->
                            TextButton (onClick = {
                                selectedCategoryId = item.id
                                sortingBy = true
                            }){
                                Text("${item.name}")
                            }
                        }
                    }

                    Row (horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()){
                        TextButton(onClick = {filterDialog = false}) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }

}

fun Long.toFormattedDate(pattern: String = "MMM dd, yyyy"): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}