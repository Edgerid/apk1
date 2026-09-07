package com.example.calorietracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.calorietracker.data.*
import com.example.calorietracker.ui.dashboard.DashboardScreen
import com.example.calorietracker.ui.log.LogScreen
import com.example.calorietracker.ui.onboarding.OnboardingScreen
import com.example.calorietracker.ui.theme.CalorieTrackerTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalorieTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var currentScreen by remember { mutableStateOf("onboarding") }
    var plan by remember { mutableStateOf(PlanStore.loadPlan(context)) }
    var foodLogs by remember { mutableStateOf(listOf<FoodLog>()) }
    var waterMl by remember { mutableIntStateOf(0) }

    // Load from database on first compose
    LaunchedEffect(Unit) {
        val db = AppDatabase.getInstance(context)
        val logs = db.foodLogDao().getLogsByDate(LocalDate.now().toString())
        foodLogs = logs.map {
            FoodLog(
                id = it.id,
                date = LocalDate.parse(it.date),
                meal = it.meal,
                name = it.name,
                serving = it.serving,
                calories = it.calories,
                protein = it.protein,
                carbs = it.carbs,
                fat = it.fat
            )
        }
    }

    when {
        plan == null || currentScreen == "onboarding" -> {
            OnboardingScreen(
                onComplete = { newPlan ->
                    plan = newPlan
                    PlanStore.savePlan(context, newPlan)
                    currentScreen = "dashboard"
                }
            )
        }
        currentScreen == "dashboard" -> {
            DashboardScreen(
                plan = plan!!,
                foodLogs = foodLogs,
                waterMl = waterMl,
                onAddWater = { change ->
                    waterMl = (waterMl + change).coerceAtLeast(0)
                },
                onLogClick = { currentScreen = "log" },
                onRemoveFood = { log ->
                    foodLogs = foodLogs.filter { it.id != log.id }
                    val db = AppDatabase.getInstance(context)
                    db.foodLogDao().deleteById(log.id)
                },
                onResetDay = {
                    foodLogs = emptyList()
                    waterMl = 0
                }
            )
        }
        currentScreen == "log" -> {
            LogScreen(
                onBack = { currentScreen = "dashboard" },
                onLogFood = { newLog ->
                    foodLogs = foodLogs + newLog
                    val db = AppDatabase.getInstance(context)
                    db.foodLogDao().insert(
                        FoodLogEntity(
                            date = newLog.date.toString(),
                            meal = newLog.meal,
                            name = newLog.name,
                            serving = newLog.serving,
                            calories = newLog.calories,
                            protein = newLog.protein,
                            carbs = newLog.carbs,
                            fat = newLog.fat
                        )
                    )
                    currentScreen = "dashboard"
                }
            )
        }
    }
}