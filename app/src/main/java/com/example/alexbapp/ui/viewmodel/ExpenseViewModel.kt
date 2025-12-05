package com.example.alexbapp.ui.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.alexbapp.data.database.ExpenseDatabase
import com.example.alexbapp.data.model.ExpenseEntity
import com.example.alexbapp.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ExpenseRepository
    private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val expenses: StateFlow<List<ExpenseEntity>> = _expenses.asStateFlow()
    
    private val _allExpenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val allExpenses: StateFlow<List<ExpenseEntity>> = _allExpenses.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<String?>("")
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    private val _customCategories = MutableStateFlow<List<String>>(emptyList())
    val customCategories: StateFlow<List<String>> = _customCategories.asStateFlow()
    
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    private val json = Json { ignoreUnknownKeys = true }
    private val CUSTOM_CATEGORIES_KEY = "custom_categories"
    
    init {
        val dao = ExpenseDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(dao)
        loadExpenses()
        loadAllExpenses()
        loadCustomCategories()
    }
    
    private fun loadSampleDataIfNeeded() {
        viewModelScope.launch {
            // Используем first() чтобы получить данные один раз, а не подписываться
            val expenses = repository.getAllExpenses().first()
            if (expenses.isEmpty()) {
                insertSampleData()
            }
        }
    }
    
    private fun insertSampleData() {
        viewModelScope.launch {
            // Sample expenses for demonstration
            val sampleExpenses = listOf(
                // November expenses
                ExpenseEntity(
                    amount = 1200.0,
                    currency = "RUB",
                    category = "Еда",
                    comment = "Продукты в магазине",
                    date = "2025-11-01"
                ),
                ExpenseEntity(
                    amount = 800.0,
                    currency = "RUB",
                    category = "Транспорт",
                    comment = "Бензин",
                    date = "2025-11-02"
                ),
                ExpenseEntity(
                    amount = 2500.0,
                    currency = "RUB",
                    category = "Развлечения",
                    comment = "Кино",
                    date = "2025-11-03"
                ),
                ExpenseEntity(
                    amount = 15000.0,
                    currency = "RUB",
                    category = "Коммунальные услуги",
                    comment = "Аренда квартиры",
                    date = "2025-11-01"
                ),
                ExpenseEntity(
                    amount = 3000.0,
                    currency = "RUB",
                    category = "Покупки",
                    comment = "Новая одежда",
                    date = "2025-11-04"
                ),
                ExpenseEntity(
                    amount = 500.0,
                    currency = "RUB",
                    category = "Еда",
                    comment = "Кофе навынос",
                    date = "2025-11-04"
                ),
                ExpenseEntity(
                    amount = 1200.0,
                    currency = "RUB",
                    category = "Транспорт",
                    comment = "Такси",
                    date = "2025-11-05"
                ),
                ExpenseEntity(
                    amount = 800.0,
                    currency = "RUB",
                    category = "Здоровье",
                    comment = "Витамины",
                    date = "2025-11-06"
                ),
                ExpenseEntity(
                    amount = 2000.0,
                    currency = "RUB",
                    category = "Еда",
                    comment = "Ресторан",
                    date = "2025-11-07"
                ),
                ExpenseEntity(
                    amount = 5000.0,
                    currency = "RUB",
                    category = "Образование",
                    comment = "Онлайн курс",
                    date = "2025-11-08"
                ),
                
                // December expenses
                ExpenseEntity(
                    amount = 950.0,
                    currency = "RUB",
                    category = "Еда",
                    comment = "Супермаркет",
                    date = "2025-12-01"
                ),
                ExpenseEntity(
                    amount = 750.0,
                    currency = "RUB",
                    category = "Транспорт",
                    comment = "Метро",
                    date = "2025-12-01"
                ),
                ExpenseEntity(
                    amount = 3200.0,
                    currency = "RUB",
                    category = "Покупки",
                    comment = "Электроника",
                    date = "2025-12-02"
                ),
                ExpenseEntity(
                    amount = 1800.0,
                    currency = "RUB",
                    category = "Развлечения",
                    comment = "Концерт",
                    date = "2025-12-03"
                ),
                ExpenseEntity(
                    amount = 1200.0,
                    currency = "RUB",
                    category = "Еда",
                    comment = "Доставка еды",
                    date = "2025-12-03"
                ),
                ExpenseEntity(
                    amount = 600.0,
                    currency = "RUB",
                    category = "Транспорт",
                    comment = "Автобус",
                    date = "2025-12-04"
                ),
                ExpenseEntity(
                    amount = 4500.0,
                    currency = "RUB",
                    category = "Коммунальные услуги",
                    comment = "Электричество",
                    date = "2025-12-05"
                ),
                ExpenseEntity(
                    amount = 1100.0,
                    currency = "RUB",
                    category = "Еда",
                    comment = "Кафе",
                    date = "2025-12-05"
                ),
                ExpenseEntity(
                    amount = 2800.0,
                    currency = "RUB",
                    category = "Покупки",
                    comment = "Косметика",
                    date = "2025-12-06"
                ),
                ExpenseEntity(
                    amount = 900.0,
                    currency = "RUB",
                    category = "Транспорт",
                    comment = "Такси",
                    date = "2025-12-06"
                ),
                
                // Some expenses in other currencies for variety
                ExpenseEntity(
                    amount = 150.0,
                    currency = "USD",
                    category = "Покупки",
                    comment = "Иностранный онлайн сервис",
                    date = "2025-12-04"
                ),
                ExpenseEntity(
                    amount = 85.0,
                    currency = "EUR",
                    category = "Развлечения",
                    comment = "Подписка на стриминг",
                    date = "2025-12-05"
                )
            )
            
            sampleExpenses.forEach { expense ->
                repository.insertExpense(expense)
            }
        }
    }
    
    fun loadExpenses() {
        expensesJob?.cancel()
        expensesJob = viewModelScope.launch {
            if (_selectedCategory.value.isNullOrEmpty()) {
                repository.getAllExpenses().collect { expenses ->
                    _expenses.value = expenses
                }
            } else {
                repository.getExpensesByCategory(_selectedCategory.value!!).collect { expenses ->
                    _expenses.value = expenses
                }
            }
        }
    }
    
    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
        loadExpenses()
    }
    
    fun insertExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }
    
    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }
    
    private var allExpensesJob: kotlinx.coroutines.Job? = null
    private var expensesJob: kotlinx.coroutines.Job? = null
    
    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            // Reload all expenses after deletion to keep UI in sync
            loadAllExpenses()
            loadExpenses()
        }
    }
    
    fun clearAllExpenses() {
        viewModelScope.launch {
            // Получаем текущий список расходов один раз, а не подписываемся на поток
            val currentExpenses = repository.getAllExpenses().first()
            currentExpenses.forEach { expense ->
                repository.deleteExpense(expense)
            }
            // Reload all expenses after clearing
            loadAllExpenses()
            loadExpenses()
        }
    }
    
    fun loadExpensesByDateRange(startDate: String, endDate: String) {
        expensesJob?.cancel()
        expensesJob = viewModelScope.launch {
            repository.getExpensesByDateRange(startDate, endDate).collect { expenses ->
                _expenses.value = expenses
            }
        }
    }
    
    fun loadAllExpenses() {
        allExpensesJob?.cancel()
        allExpensesJob = viewModelScope.launch {
            repository.getAllExpenses().collect { expenses ->
                _allExpenses.value = expenses
            }
        }
    }
    
    fun getAllExpenses() {
        expensesJob?.cancel()
        expensesJob = viewModelScope.launch {
            repository.getAllExpenses().collect { expenses ->
                _expenses.value = expenses
            }
        }
    }
    
    // Custom Categories Management
    private fun loadCustomCategories() {
        try {
            val categoriesJson = sharedPreferences.getString(CUSTOM_CATEGORIES_KEY, "[]")
            val categories = json.decodeFromString<List<String>>(categoriesJson ?: "[]")
            _customCategories.value = categories
        } catch (e: Exception) {
            _customCategories.value = emptyList()
        }
    }
    
    fun addCustomCategory(category: String) {
        val currentCategories = _customCategories.value.toMutableList()
        if (!currentCategories.contains(category)) {
            currentCategories.add(category)
            _customCategories.value = currentCategories
            saveCustomCategories(currentCategories)
        }
    }
    
    fun removeCustomCategory(category: String) {
        val currentCategories = _customCategories.value.toMutableList()
        currentCategories.remove(category)
        _customCategories.value = currentCategories
        saveCustomCategories(currentCategories)
    }
    
    private fun saveCustomCategories(categories: List<String>) {
        try {
            val categoriesJson = json.encodeToString(categories)
            sharedPreferences.edit().putString(CUSTOM_CATEGORIES_KEY, categoriesJson).apply()
        } catch (e: Exception) {
            // Handle error silently
        }
    }
    
    fun getAllCategories(): List<String> {
        val predefinedCategories = listOf("Еда", "Транспорт", "Развлечения", "Коммунальные услуги", "Другое")
        return predefinedCategories + _customCategories.value
    }
    
    // Public method to load sample data on demand
    fun loadSampleData() {
        viewModelScope.launch {
            // Check if database is empty first
            val expenses = repository.getAllExpenses().first()
            if (expenses.isEmpty()) {
                insertSampleData()
            }
        }
    }
}
