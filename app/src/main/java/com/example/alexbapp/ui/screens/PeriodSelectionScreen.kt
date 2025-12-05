package com.example.alexbapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alexbapp.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodSelectionScreen(
    navController: NavController
) {
    var startDate by remember { mutableStateOf(LocalDate.now().minusDays(30)) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var startDateText by remember { mutableStateOf(startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))) }
    var endDateText by remember { mutableStateOf(endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))) }
    
    val periods = listOf(
        stringResource(R.string.last_7_days) to 7L,
        stringResource(R.string.last_30_days) to 30L,
        stringResource(R.string.last_90_days) to 90L,
        stringResource(R.string.current_month) to -1L,
        stringResource(R.string.current_year) to -2L,
        stringResource(R.string.custom_period) to -3L
    )
    
    var selectedPeriod by remember { mutableStateOf(periods[1]) } // Default to 30 days
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_period)) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Predefined periods
            Text(
                text = stringResource(R.string.quick_selection),
                style = MaterialTheme.typography.titleMedium
            )
            
            periods.filter { it.second > 0 }.forEach { period ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        selectedPeriod = period
                        // Calculate dates based on period
                        val today = LocalDate.now()
                        val newStartDate = if (period.second > 0) {
                            today.minusDays(period.second)
                        } else {
                            today
                        }
                        startDate = newStartDate
                        endDate = today
                        startDateText = newStartDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        endDateText = today.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        
                        // Navigate to chart screen with selected period
                        navController.navigate("expense_chart/${newStartDate}/${today}")
                    },
                    colors = if (selectedPeriod == period) {
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    } else {
                        CardDefaults.cardColors()
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = period.first,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Custom period selection
            Text(
                text = stringResource(R.string.custom_period),
                style = MaterialTheme.typography.titleMedium
            )
            
            // Start date picker placeholder
            OutlinedTextField(
                value = startDateText,
                onValueChange = { startDateText = it },
                label = { Text(stringResource(R.string.start_date)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        // TODO: Implement date picker
                    }) {
                        // Calendar icon would go here
                    }
                }
            )
            
            // End date picker placeholder
            OutlinedTextField(
                value = endDateText,
                onValueChange = { endDateText = it },
                label = { Text(stringResource(R.string.end_date)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        // TODO: Implement date picker
                    }) {
                        // Calendar icon would go here
                    }
                }
            )
            
            // Apply button for custom period
            Button(
                onClick = {
                    // Parse dates and navigate to chart
                    try {
                        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        val parsedStartDate = LocalDate.parse(startDateText, formatter)
                        val parsedEndDate = LocalDate.parse(endDateText, formatter)
                        navController.navigate("expense_chart/$parsedStartDate/$parsedEndDate")
                    } catch (e: Exception) {
                        // Handle parsing error
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = startDateText.isNotEmpty() && endDateText.isNotEmpty()
            ) {
                Text(stringResource(R.string.view_chart))
            }
        }
    }
}
