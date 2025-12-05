package com.example.alexbapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val currency: String = "RUB",
    val category: String,
    val comment: String? = null,
    val date: String,
    val createdAt: Long = System.currentTimeMillis()
)
