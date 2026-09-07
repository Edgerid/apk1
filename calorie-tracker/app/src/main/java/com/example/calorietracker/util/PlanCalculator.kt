package com.example.calorietracker.util

import com.example.calorietracker.data.Plan

object PlanCalculator {
    fun calculate(
        name: String,
        age: Int,
        sex: String,
        heightCm: Float,
        weightKg: Float,
        goalWeight: Float?,
        goal: String,
        activityLevel: String,
        pace: String,
        eatingStyle: String,
        foodsToAvoid: String
    ): Plan {
        // BMR via Mifflin-St Jeor
        val bmr = if (sex == "Male") {
            10 * weightKg + 6.25 * heightCm - 5 * age + 5
        } else {
            10 * weightKg + 6.25 * heightCm - 5 * age - 161
        }

        // TDEE
        val tdee = when (activityLevel) {
            "Sedentary" -> bmr * 1.2f
            "Lightly active" -> bmr * 1.375f
            "Moderately active" -> bmr * 1.55f
            "Very active" -> bmr * 1.725f
            "Athlete" -> bmr * 1.9f
            else -> bmr * 1.375f
        }

        // Calorie target
        val dailyCalories = when (goal) {
            "Lose weight" -> {
                val maxDeficit = if (pace == "Aggressive") 750 else if (pace == "Steady") 500 else 250
                val minDeficit = if (pace == "Aggressive") 500 else if (pace == "Steady") 350 else 250
                val deficit = ((maxDeficit + minDeficit) / 2).toInt()
                val target = (tdee - deficit).toInt()
                if (sex == "Male") maxOf(target, 1500) else maxOf(target, 1200)
            }
            "Gain weight" -> {
                val surplus = when (pace) {
                    "Aggressive" -> 400
                    "Steady" -> 325
                    else -> 250
                }
                (tdee + surplus).toInt()
            }
            else -> tdee.toInt() // Maintain
        }

        // Macros
        val weightLbs = weightKg * 2.20462f
        val proteinG = if (goal == "Lose weight" || goal == "Gain weight") {
            (weightLbs * 1.0f).toInt()
        } else {
            (weightLbs * 0.8f).toInt()
        }
        val fatG = (weightLbs * 0.35f).toInt()
        val proteinCals = proteinG * 4
        val fatCals = fatG * 9
        val carbCals = dailyCalories - proteinCals - fatCals
        val carbsG = maxOf(carbCals / 4, 0)

        // Water
        val waterLiters = (weightLbs * 0.75f) / 33.814f // oz to liters

        // Timeline to goal
        val weeksToGoal = if (goal != "Maintain" && goalWeight != null && goalWeight > 0f) {
            val weeklyGap = (tdee - dailyCalories) * 7f
            val weeklyWeightChange = if (goal == "Lose weight") {
                // deficits are positive when losing
                kotlin.math.abs(weeklyGap) / 3500f
            } else {
                // surplus means gaining
                kotlin.math.abs(weeklyGap) / 3500f
            }
            val weeks = kotlin.math.abs(weightKg * 2.20462f - goalWeight) / weeklyWeightChange
            kotlin.math.ceil(weeks.toDouble()).toInt()
        } else null

        return Plan(
            name = name,
            age = age,
            sex = sex,
            heightCm = heightCm,
            weightKg = weightKg,
            goalWeight = goalWeight,
            goal = goal,
            activityLevel = activityLevel,
            pace = pace,
            eatingStyle = eatingStyle,
            foodsToAvoid = foodsToAvoid,
            dailyCalories = dailyCalories,
            protein = proteinG,
            carbs = carbsG,
            fat = fatG,
            waterLiters = waterLiters,
            weekstoGoal = weeksToGoal
        )
    }
}