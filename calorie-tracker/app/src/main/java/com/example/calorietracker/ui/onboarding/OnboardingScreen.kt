package com.example.calorietracker.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietracker.util.PlanCalculator

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: (com.example.calorietracker.data.Plan) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("Male") }
    var heightCm by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("Maintain") }
    var goalWeight by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("Lightly active") }
    var pace by remember { mutableStateOf("Steady") }
    var eatingStyle by remember { mutableStateOf("No preference") }
    var foodsToAvoid by remember { mutableStateOf("") }

    val steps = listOf(
        "What's your name?",
        "How old are you?",
        "Biological sex (for metabolic math)",
        "Your height in cm",
        "Your current weight in kg",
        "Your goal",
        "Target weight in kg",
        "Activity level",
        "Pace preference",
        "Eating style",
        "Any foods to avoid?"
    )

    val progress = (step + 1).toFloat() / (steps.size + 1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color(0xFF2563EB),
            trackColor = Color(0xFFE0E7FF)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (step == 0) {
            // Welcome screen
            Text(
                "Hi! I'm your nutrition coach.\nLet's get your plan dialed in. 💪",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "I'm an AI assistant, not a medical professional.\nCheck with your doctor before starting any new diet.",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Let's ask a few quick questions\nto build a plan that fits you.",
                fontSize = 16.sp,
                color = Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(48.dp))
        }

        Text(
            steps[step],
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Step content
        when (step) {
            0 -> NameField(name) { name = it }
            1 -> AgeField(age) { age = it }
            2 -> SexSelector(sex) { sex = it }
            3 -> HeightField(heightCm) { heightCm = it }
            4 -> WeightField(weightKg) { weightKg = it }
            5 -> GoalSelector(goal) { goal = it }
            6 -> GoalWeightField(goalWeight, goal) { goalWeight = it }
            7 -> ActivitySelector(activityLevel) { activityLevel = it }
            8 -> PaceSelector(pace) { pace = it }
            9 -> EatingStyleSelector(eatingStyle) { eatingStyle = it }
            10 -> FoodsField(foodsToAvoid) { foodsToAvoid = it }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (step > 0) {
                TextButton(onClick = { step-- }) {
                    Text("← Back", color = Color(0xFF64748B))
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
                onClick = {
                    if (step < steps.size - 1) {
                        step++
                    } else {
                        // Calculate and return plan
                        val plan = PlanCalculator.calculate(
                            name = name,
                            age = age.toIntOrNull() ?: 30,
                            sex = sex,
                            heightCm = heightCm.toFloatOrNull() ?: 170f,
                            weightKg = weightKg.toFloatOrNull() ?: 70f,
                            goalWeight = goalWeight.toFloatOrNull(),
                            goal = goal,
                            activityLevel = activityLevel,
                            pace = pace,
                            eatingStyle = eatingStyle,
                            foodsToAvoid = foodsToAvoid
                        )
                        onComplete(plan)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    if (step == steps.size - 1) "Calculate Plan" else "Next →",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun NameField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Your name") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun AgeField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Age") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}

@Composable
fun SexSelector(value: String, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        listOf("Male", "Female").forEach { option ->
            FilterChip(
                selected = value == option,
                onClick = { onSelect(option) },
                label = { Text(option) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2563EB),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun HeightField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Height (cm)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}

@Composable
fun WeightField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Weight (kg)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true
    )
}

@Composable
fun GoalSelector(value: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("Lose weight", "Maintain", "Gain weight").forEach { option ->
            FilterChip(
                selected = value == option,
                onClick = { onSelect(option) },
                label = { Text(option) },
                modifier = Modifier.fillMaxWidth(),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2563EB),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun GoalWeightField(value: String, goal: String, onValueChange: (String) -> Unit) {
    if (goal != "Maintain") {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Target weight (kg)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
    } else {
        Text("No target weight needed for maintenance!", color = Color(0xFF64748B))
    }
}

@Composable
fun ActivitySelector(value: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("Sedentary", "Lightly active", "Moderately active", "Very active", "Athlete").forEach { option ->
            FilterChip(
                selected = value == option,
                onClick = { onSelect(option) },
                label = { Text(option) },
                modifier = Modifier.fillMaxWidth(),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2563EB),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun PaceSelector(value: String, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        listOf("Gentle", "Steady", "Aggressive").forEach { option ->
            FilterChip(
                selected = value == option,
                onClick = { onSelect(option) },
                label = { Text(option) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2563EB),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun EatingStyleSelector(value: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("No preference", "High protein", "Low carb", "Vegetarian", "Vegan").forEach { option ->
            FilterChip(
                selected = value == option,
                onClick = { onSelect(option) },
                label = { Text(option) },
                modifier = Modifier.fillMaxWidth(),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2563EB),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun FoodsField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Foods to avoid or health notes (optional)") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 3
    )
}