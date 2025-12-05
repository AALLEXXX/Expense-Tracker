package com.example.alexbapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alexbapp.R
import com.example.alexbapp.ui.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseChartScreen(
    navController: NavController,
    viewModel: ExpenseViewModel,
    startDate: String,
    endDate: String
) {
    val expenses by viewModel.expenses.collectAsState()
    
    // Calculate category totals
    val categoryTotals = remember(expenses) {
        val totals = mutableMapOf<String, Double>()
        expenses.forEach { expense ->
            val currentTotal = totals.getOrDefault(expense.category, 0.0)
            totals[expense.category] = currentTotal + expense.amount
        }
        totals
    }
    
    val chartColors = listOf(
        Color(0xFFE91E63), // Pink
        Color(0xFF2196F3), // Blue
        Color(0xFF4CAF50), // Green
        Color(0xFFFFC107), // Yellow
        Color(0xFF9C27B0), // Purple
        Color(0xFF00BCD4), // Cyan
        Color(0xFFFF5722), // Orange
        Color(0xFF795548), // Brown
        Color(0xFF607D8B), // Blue Grey
        Color(0xFFCDDC39)  // Lime
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.expense_chart)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Period info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.period),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$startDate - $endDate",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = stringResource(R.string.total_expenses),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${String.format("%.2f", categoryTotals.values.sum())} ₽",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (categoryTotals.isEmpty()) {
                // No data message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_expenses_for_period),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // Pie chart
                PieChart(
                    data = categoryTotals,
                    colors = chartColors
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Category breakdown
                Text(
                    text = stringResource(R.string.expense_breakdown),
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                categoryTotals.entries.sortedByDescending { it.value }.forEach { (category, amount) ->
                    CategoryItem(
                        category = category,
                        amount = amount,
                        percentage = (amount / categoryTotals.values.sum()) * 100,
                        color = chartColors[categoryTotals.keys.indexOf(category) % chartColors.size]
                    )
                }
            }
        }
    }
}

@Composable
fun PieChart(
    data: Map<String, Double>,
    colors: List<Color>,
    radius: Dp = 100.dp
) {
    val total = data.values.sum()
    val proportions = data.values.map { (it / total).toFloat() }.toFloatArray()
    
    Box(
        modifier = Modifier
            .size(radius * 2)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(radius * 2)
        ) {
            val canvasSize = size.minDimension
            val radiusPx = canvasSize / 2
            val center = Offset(canvasSize / 2, canvasSize / 2)
            
            var startAngle = -90f
            
            proportions.forEachIndexed { index, proportion ->
                val sweepAngle = proportion * 360f
                val color = colors[index % colors.size]
                
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(canvasSize, canvasSize),
                    topLeft = Offset.Zero
                )
                
                // Draw border
                drawArc(
                    color = Color.White,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(canvasSize, canvasSize),
                    topLeft = Offset.Zero,
                    style = Stroke(width = 2f)
                )
                
                startAngle += sweepAngle
            }
        }
        
        // Center text with total
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Всего",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = data.values.sum().toInt().toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: String,
    amount: Double,
    percentage: Double,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${String.format("%.2f", amount)} ₽",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${String.format("%.1f", percentage)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
