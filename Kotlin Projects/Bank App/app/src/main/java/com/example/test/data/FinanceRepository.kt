package com.example.test.data

import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val categoryDao: CategoryDao, private val transactionDao: TransactionDao) {
//categoryDao services
    fun getAllCategories() = categoryDao.getAllCategories()
    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)
    suspend fun insertCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)


//transactionDao services
    fun getAllTransactions() = transactionDao.getAllTransactions()
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> = transactionDao.getTransactionsByCategory(categoryId)
   suspend fun getTransactionById(id: Long): Transaction? = transactionDao.getTransactionById(id)
    suspend fun getTotalByTypeAndDateRange(type: TransactionType, startDate: Long, endDate: Long): Double? = transactionDao.getTotalByTypeAndDateRange(type,startDate,endDate)
    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insertTransaction(transaction)
    suspend fun updateTransaction(transaction: Transaction) = transactionDao.updateTransaction(transaction)
    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.deleteTransaction(transaction)

//unused functions
    fun getTransactionsByDateRange(startDate: Long, endDate: Long) = transactionDao.getTransactionsByDateRange(startDate,endDate)
    fun getTransactionsByCategoryAndDateRange(categoryId: Long, startDate: Long, endDate: Long) = transactionDao.getTransactionsByCategoryAndDateRange(categoryId,startDate,endDate)
    suspend fun deleteTransactionById(id: Long) = transactionDao.deleteTransactionById(id)
    suspend fun getTotalByCategoryAndDateRange(categoryId: Long, startDate: Long, endDate: Long) = transactionDao.getTotalByCategoryAndDateRange(categoryId,startDate,endDate)
    suspend fun deleteCategoryById(id: Long) = categoryDao.deleteCategoryById(id)
    fun getCategoriesByType(type: TransactionType) = categoryDao.getCategoriesByType(type)
}