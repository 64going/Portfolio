package com.example.test.viewmodel

import android.app.Application
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test.data.Category
import com.example.test.data.FinanceDatabase
import com.example.test.data.FinanceRepository
import com.example.test.data.Transaction
import com.example.test.data.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {


    suspend fun getCategoryById(id: Long): Category? {
        return repository.getCategoryById(id)
    }
    suspend fun getTransactionById(id: Long): Transaction? {
          return repository.getTransactionById(id)
    }


    val transactions: StateFlow<List<Transaction>> = repository.getAllTransactions().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000), // Start collecting when there's an active subscriber and stop 5s after the last subscriber disappears.
        emptyList()
    )
    val categorys: StateFlow<List<Category>> = repository.getAllCategories().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000), // Start collecting when there's an active subscriber and stop 5s after the last subscriber disappears.
        emptyList()
    )
    fun insertCategory (category: Category){
        viewModelScope.launch {
            repository.insertCategory(Category(
                name = category.name,
                type = category.type,
                color = category.color,
            ))
        }
    }
    fun deleteCategory (category: Category){
        viewModelScope.launch {
                repository.deleteCategory(category)

        }
    }
    fun updateCategory(category: Category){
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }
    fun updateTransaction(transaction: Transaction){
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }
    fun deleteTransactionById(id: Long) {
        viewModelScope.launch {
            repository.deleteTransactionById(id)
        }
    }

    fun getTransactionsByCategory(categoryId: Long) :Flow<List<Transaction>>{
        return repository.getTransactionsByCategory(categoryId)
    }

    fun deleteTransaction(transaction: Transaction){
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
    fun insertTransaction(transaction: Transaction){
        viewModelScope.launch {
            repository.insertTransaction(Transaction(
                amount = transaction.amount,
                date = transaction.date,
                description = transaction.description,
                categoryId = transaction.categoryId,
                type = transaction.type
            ))
        }
    }
    fun getTotalByTypeAndDateRange(
        type: TransactionType,
        startDate: Long,
        endDate: Long,
        onResult: (Double?) -> Unit
    ) {
        viewModelScope.launch {
            val total = repository.getTotalByTypeAndDateRange(type, startDate, endDate)
            onResult(total)
        }
    }
    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory {
            // Create a NoteRepository, which in turn depends on NoteDao from NoteDatabase.
            return FinanceViewModelFactory(FinanceRepository(FinanceDatabase.getDatabase(application).categoryDao(),FinanceDatabase.getDatabase(application).transactionDao()))
        }
    }
}

class FinanceViewModelFactory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
    // The create method is responsible for creating new ViewModel instances.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class is NoteViewModel.
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            // If it is, create and return a new NoteViewModel instance.
            @Suppress("UNCHECKED_CAST") // Suppress the unchecked cast warning as we've checked the type.
            return FinanceViewModel(repository) as T
        }
        // If an unknown ViewModel class is requested, throw an exception.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}