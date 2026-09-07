package com.example.calorietracker.data

import java.time.LocalDate

data class Plan(
    val name: String,
    val age: Int,
    val sex: String,
    val heightCm: Float,
    val weightKg: Float,
    val goalWeight: Float?,
    val goal: String, // lose, maintain, gain
    val activityLevel: String,
    val pace: String, // gentle, steady, aggressive
    val eatingStyle: String,
    val foodsToAvoid: String,
    val dailyCalories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val waterLiters: Float,
    val weekstoGoal: Int?
)

data class FoodLog(
    val id: Long = System.currentTimeMillis(),
    val date: LocalDate,
    val meal: String, // Breakfast, Lunch, Dinner, Snacks
    val name: String,
    val serving: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)