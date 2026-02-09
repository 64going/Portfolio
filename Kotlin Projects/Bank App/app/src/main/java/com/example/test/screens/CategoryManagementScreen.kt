package com.example.test.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.test.data.Category
import com.example.test.data.TransactionType
import com.example.test.viewmodel.FinanceViewModel
import androidx.core.graphics.toColorInt


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryManagementScreen(viewModel: FinanceViewModel, onBack: () -> Unit) {
    val categorys by viewModel.categorys.collectAsState()
    var showDialogDelete by remember { mutableStateOf(false) }
    var showInfoDelete by remember { mutableStateOf(false) }
    var categoryClicked: Category by remember {
        mutableStateOf(
            Category(
                name = "asd",
                type = TransactionType.EXPENSE
            )
        )
    }
    var editCategory by remember { mutableStateOf(false) }
    var addCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Manage Categories") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = { addCategoryDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add"
                    )
                }
            }
        )
    }) { padding ->

        LazyColumn(modifier = Modifier
            .fillMaxHeight()
            .padding(padding)
            .padding(16.dp)) {
            items(categorys.size) { item ->
                val categoryItem = categorys[item]
                val transactions by viewModel
                    .getTransactionsByCategory(categoryItem.id)
                    .collectAsState(initial = emptyList())
                val category by produceState<Category?>(
                    initialValue = null,
                    key1 = categoryItem.id
                ) {
                    value = viewModel.getCategoryById(categoryItem.id)
                }
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            if (category != null) {
                                categoryClicked = category!!
                            }
                            editCategory = true
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = Color(categoryItem.color.toColorInt()),
                                        CircleShape
                                    )
                            )
                            Column() {
                                Text(
                                    categoryItem.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (categoryItem.type.toString() == "EXPENSE") {
                                        "Expense"
                                    } else {
                                        "Income"
                                    },
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        IconButton(onClick = { categoryClicked = categoryItem;if(transactions.isEmpty()){showDialogDelete = true}else{showInfoDelete = true} }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    if (addCategoryDialog) {
        addCategoryDialog = alertDialog(
            viewModel = viewModel,
            add = "Add"
        )
    }
    if (editCategory) {
        editCategory = alertDialog(
            viewModel = viewModel,
            add = "Edit",
            category = categoryClicked,
            bool = true
        )
    }
    if (showDialogDelete){
        showDialogDelete = deleteDialog(viewModel, category = categoryClicked)
    }
    if (showInfoDelete){
        showInfoDelete = infoDialog(categoryClicked)
    }

}
@Composable
fun infoDialog(category: Category): Boolean{
    var returnBoolean by remember { mutableStateOf(true) }
    var showDialogDelete by remember { mutableStateOf(true) }
    if(showDialogDelete){
        AlertDialog(
            onDismissRequest = { showDialogDelete = false },
            title = { Text("Cannot Delete Category") },
            text = { Text("Cannot delete \"${category.name}\" because it has associated transactions. Please delete or reassign all transactions in this category first") },
            confirmButton = {
                TextButton(onClick = { showDialogDelete = false ;returnBoolean = false}) {
                    Text("Ok", color = MaterialTheme.colorScheme.primary)
                } }
        )
    }
    return returnBoolean
}
@Composable
fun deleteDialog(viewModel: FinanceViewModel,category: Category): Boolean{
    var returnBoolean by remember { mutableStateOf(true) }
    var showDialogDelete by remember { mutableStateOf(true) }
    if(showDialogDelete){
        AlertDialog(
            onDismissRequest = { showDialogDelete = false },
            title = { Text("Delete Category") },
            text = { Text("Are you sure you want to delete \"${category.name}\"? This action can't be undone.") },
            confirmButton = {
                TextButton(onClick = { showDialogDelete = false ;viewModel.deleteCategory(category);returnBoolean = false}) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                } },
            dismissButton = {
                TextButton(onClick = { showDialogDelete = false;returnBoolean = false}) {
                    Text("Cancel")
                }
            }
        )
    }
    return returnBoolean
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun alertDialog(
    viewModel: FinanceViewModel,
    add: String = "Add",
    category: Category? = null,
    bool: Boolean = false
): Boolean{
    var editCategory by remember { mutableStateOf(bool) }
    var returnBoolean by remember { mutableStateOf(true) }
    val type = listOf<TransactionType>(TransactionType.EXPENSE, TransactionType.INCOME)
    var addCategoryDialog by remember { mutableStateOf(true) }
    var selectedCategory = remember { mutableStateListOf<TransactionType>() }
    var selectedColor = remember { mutableStateListOf<String>() }
    var addCategoryName by remember { mutableStateOf("") }
    val colors = listOf("#6200EE", "#03DAC5", "#3700B3", "#018786", "#000000")
    if (editCategory) {
        addCategoryName = category!!.name
    }
    if (editCategory) {
        selectedCategory = remember { mutableStateListOf<TransactionType>(category!!.type) }
    }
    if (editCategory) {
        selectedColor = remember { mutableStateListOf<String>(category!!.color) }
    }
        if (addCategoryDialog) {
            BasicAlertDialog(
                onDismissRequest = { addCategoryDialog = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                content = {
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(24.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("$add Category", style = MaterialTheme.typography.titleLarge)

                            OutlinedTextField(
                                value = addCategoryName,
                                onValueChange = { addCategoryName = it },
                                label = { Text("Category Name") }
                            )
                            Text("Type", style = MaterialTheme.typography.titleSmall)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                type.forEach { item ->

                                    FilterChip(
                                        selected = item in selectedCategory,
                                        onClick = {
                                            if (selectedCategory.isNotEmpty()) selectedCategory.removeAll(
                                                selectedCategory
                                            )
                                            if (item in selectedCategory) selectedCategory.remove(
                                                item
                                            )
                                            else selectedCategory.add(item)
                                        },
                                        label = {
                                            Text(
                                                if (item.name == "EXPENSE") {
                                                    "Expense"
                                                } else {
                                                    "Income"
                                                }
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }
                            }
                            Text("Color", style = MaterialTheme.typography.titleSmall)
                            FlowRow(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                colors.forEach { item ->
                                    FilterChip(
                                        selected = item in selectedColor,
                                        onClick = {
                                            if (selectedColor.isNotEmpty()) selectedColor.removeAll(
                                                selectedColor
                                            )
                                            if (item in selectedColor) selectedColor.remove(item)
                                            else selectedColor.add(item)
                                        },
                                        label = { },
                                        shape = CircleShape,
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = Color(item.toColorInt()),
                                            selectedContainerColor = Color(item.toColorInt())
                                        ),
                                        modifier = Modifier.size(40.dp),
                                        border = FilterChipDefaults.filterChipBorder(
                                            borderColor = Color(item.toColorInt()),
                                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                                            enabled = true,
                                            selected = item in selectedColor,
                                            selectedBorderWidth = 1.5.dp
                                        )
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { addCategoryDialog = false ;returnBoolean = false}) {
                                    Text("Cancel")
                                }
                                TextButton(onClick = {
                                    returnBoolean = false
                                    addCategoryDialog = false;
                                    if (editCategory) {
                                        val updatedCategory = category!!.copy(name = addCategoryName, type = selectedCategory.single(), color = selectedColor.single())
                                        viewModel.updateCategory(updatedCategory)
                                    } else {
                                        viewModel.insertCategory(
                                            Category(
                                                name = addCategoryName,
                                                type = selectedCategory.single(),
                                                color = selectedColor.single()
                                            )
                                        )
                                    }

                                }, enabled = addCategoryName.isNotEmpty()) {
                                    Text("save")

                                }
                            }
                        }
                    }
                })
        }

    return returnBoolean
}