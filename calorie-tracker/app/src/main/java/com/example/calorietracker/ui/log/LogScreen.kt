package com.example.calorietracker.ui.log

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietracker.data.FoodDatabase
import com.example.calorietracker.data.FoodLog
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    onBack: () -> Unit,
    onLogFood: (FoodLog) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMeal by remember { mutableStateOf("Lunch") }
    val searchResults = remember(searchQuery) {
        if (searchQuery.length >= 2) FoodDatabase.search(searchQuery) else emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Food") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Meal selector
            Text(
                "Select meal:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Breakfast", "Lunch", "Dinner", "Snacks").forEach { meal ->
                    FilterChip(
                        selected = selectedMeal == meal,
                        onClick = { selectedMeal = meal },
                        label = { Text(meal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search food...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search results
            if (searchResults.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults) { food ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        food.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        "${food.serving} • ${food.calories} cal",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "${food.protein.toInt()}P • ${food.carbs.toInt()}C • ${food.fat.toInt()}F",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onLogFood(
                                            FoodLog(
                                                date = LocalDate.now(),
                                                meal = selectedMeal,
                                                name = food.name,
                                                serving = food.serving,
                                                calories = food.calories,
                                                protein = food.protein,
                                                carbs = food.carbs,
                                                fat = food.fat
                                            )
                                        )
                                        searchQuery = ""
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add food",
                                        tint = Color(0xFF2563EB)
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (searchQuery.length >= 2) {
                Text(
                    "No results found",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Quick add button
            if (searchQuery.isNotEmpty()) {
                Button(
                    onClick = {
                        onLogFood(
                            FoodLog(
                                date = LocalDate.now(),
                                meal = selectedMeal,
                                name = searchQuery,
                                serving = "custom",
                                calories = 150, // placeholder
                                protein = 10f,
                                carbs = 20f,
                                fat = 5f
                            )
                        )
                        searchQuery = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Quick Add: $searchQuery")
                }
            }
        }
    }
}