package com.example.test.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.test.data.Category
import com.example.test.data.Transaction
import com.example.test.data.TransactionType
import com.example.test.viewmodel.FinanceViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionScreen(viewModel: FinanceViewModel, onBack: () -> Unit, add :String = "Add", transactionId: String = "null"){
    var isEditing by remember { mutableStateOf(transactionId != "null") }

    val optionsForType = listOf(TransactionType.EXPENSE,TransactionType.INCOME)
    var selectedType = remember { mutableStateListOf(TransactionType.EXPENSE) }
    val categorys by viewModel.categorys.collectAsState()
    var selectedCategory = remember { mutableStateListOf<Category?>()}
    var isCategorySelected by remember { mutableStateOf(false)}
    var descriptionTextField by remember { mutableStateOf("") }
    var amountTextField by remember { mutableStateOf("") }
    var descriptionTextFieldError by remember { mutableStateOf(false) }
    var amountTextFieldError by remember { mutableStateOf(false) }
    var datePickerDialog by remember { mutableStateOf(false) }
    var transactionClicked: Transaction by remember {
        mutableStateOf(
            Transaction(
                type = TransactionType.EXPENSE,
                amount = 0.0,
                date = 1000000,
                description = "dfaultt",
                categoryId = 0,
            )
        )
    }

    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val formatterToMills = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
    var selectedDate by remember { mutableStateOf<String>(LocalDate.now().format(formatter)) }

    if (isEditing) {
        val transaction by produceState<Transaction?>(initialValue = null, key1 = transactionId.toLong()) {
            value = viewModel.getTransactionById(transactionId.toLong())
        }

        val category by produceState<Category?>(initialValue = null, key1 = transaction?.categoryId) {
            transaction?.let {
                value = viewModel.getCategoryById(it.categoryId)
            }
        }
        if (transaction != null && category != null) {
             selectedType = remember { mutableStateListOf(transaction!!.type) }
             selectedCategory = remember { mutableStateListOf<Category?>(category) }
             descriptionTextField = transaction!!.description
             amountTextField = transaction!!.amount.toString()
             selectedDate = transaction!!.date.toFormattedDate()
            transactionClicked = transaction!!
        } else {
        }

    }
    Scaffold (topBar = {TopAppBar(
        title = {Text("$add Transaction")},
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer, titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer),
        navigationIcon = {IconButton(onClick = {onBack()}){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        } },
        actions = {
            TextButton(onClick = {isCategorySelected = selectedCategory.isEmpty();descriptionTextFieldError = descriptionTextField.isEmpty();amountTextFieldError = amountTextField.isEmpty();

                val millis = LocalDate.parse(selectedDate,formatterToMills)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
                viewModel.insertTransaction(Transaction(
                    amount = amountTextField.toDouble(),
                    date = millis,
                    description = descriptionTextField,
                    categoryId = selectedCategory.single()!!.id,
                    type = selectedType.single()
                )) ;onBack()
             }){
                Text("Save", style = MaterialTheme.typography.titleMedium)
            }
        }
    )}){ padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
            Text("Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                optionsForType.forEach { item ->
                    FilterChip(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        selected = item in selectedType,
                        onClick = {
                            if (selectedType.isNotEmpty()) selectedType.removeAll(selectedType)
                            if (item in selectedType) selectedType.remove(item)
                            else selectedType.add(item)
                        },
                        label = { if(item.name == "EXPENSE"){Text("Expense")}else{Text("Income")} },
                        leadingIcon = if (item in selectedType) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }
            Text("Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if(isCategorySelected){Text("Please select a category", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)}
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorys.forEach { item ->
                    FilterChip(
                        selected = item in selectedCategory,
                        onClick = {
                            if (selectedCategory.isNotEmpty()) selectedCategory.removeAll(selectedCategory)
                            if (item in selectedCategory) selectedCategory.remove(item)
                            else selectedCategory.add(item)
                            isCategorySelected = selectedCategory.isEmpty()
                        },
                        label = { Text(item.name) }
                    )
                }
            }
            OutlinedTextField(
                value = descriptionTextField,
                onValueChange = { descriptionTextField = it ;descriptionTextFieldError = descriptionTextField.isEmpty()},
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                isError = descriptionTextFieldError,
                supportingText = {if(descriptionTextFieldError){Text("Description is required", color = MaterialTheme.colorScheme.error)}}
            )
            OutlinedTextField(
                value = amountTextField,
                onValueChange = { amountTextField = it ;amountTextFieldError = amountTextField.isEmpty()},
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                isError = amountTextFieldError,
                supportingText = {if(amountTextFieldError){Text("Amount is required", color = MaterialTheme.colorScheme.error)}}
            )
            Text("Date", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)


            Card(modifier = Modifier.clickable{datePickerDialog = true}.fillMaxWidth()) {
                Text(selectedDate, modifier = Modifier.padding(8.dp))
            }

            Button(
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                onClick = {
                    val millis = LocalDate.parse(selectedDate,formatterToMills)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                    if (isEditing){
                        val updatedTransaction = transactionClicked.copy(
                            amount = amountTextField.toDouble(),
                            description = descriptionTextField,
                            type = selectedType.single(),
                            date = millis,
                            categoryId = selectedCategory.single()!!.id)
                        viewModel.updateTransaction(updatedTransaction)
                    } else {
                    ;
                    viewModel.insertTransaction(Transaction(
                    amount = amountTextField.toDouble(),
                    date = millis,
                    description = descriptionTextField,
                    categoryId = selectedCategory.single()!!.id,
                    type = selectedType.single()))
                    }
                onBack()
                },
                enabled = descriptionTextField.isNotEmpty() && amountTextField.isNotEmpty() && selectedCategory.isNotEmpty()
            ) {
                Icon(imageVector = Icons.Filled.Add,
                    contentDescription = "add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Transaction")
            }

            if (datePickerDialog) {
                val datePickerState = rememberDatePickerState()
                val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                DatePickerDialog(
                    onDismissRequest = { datePickerDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val millis = datePickerState.selectedDateMillis
                            if (millis != null) {
                                selectedDate =
                                    Instant.ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                        .format(formatter)
                            }
                            datePickerDialog = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { datePickerDialog = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}