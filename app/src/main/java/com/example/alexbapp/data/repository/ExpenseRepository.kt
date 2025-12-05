package com.example.alexbapp.data.repository

import com.example.alexbapp.data.dao.ExpenseDao
import com.example.alexbapp.data.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    
    fun getAllExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses()
    }
    
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByCategory(category)
    }
    
    suspend fun insertExpense(expense: ExpenseEntity) {
        expenseDao.insert(expense)
    }
    
    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.update(expense)
    }
    
    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.delete(expense)
    }
    
    fun getExpensesByDateRange(startDate: String, endDate: String): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByDateRange(startDate, endDate)
    }
}
