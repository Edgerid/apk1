package com.example.calorietracker.data

import android.content.Context
import androidx.room.*

@Entity(tableName = "food_logs")
data class FoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // ISO date string YYYY-MM-DD
    val meal: String,
    val name: String,
    val serving: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)

@Dao
interface FoodLogDao {
    @Query("SELECT * FROM food_logs WHERE date = :date ORDER BY meal")
    fun getLogsByDate(date: String): List<FoodLogEntity>

    @Insert
    fun insert(log: FoodLogEntity)

    @Delete
    fun delete(log: FoodLogEntity)

    @Query("DELETE FROM food_logs WHERE id = :id")
    fun deleteById(id: Long)
}

@Database(entities = [FoodLogEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodLogDao(): FoodLogDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calorie_tracker.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

object PlanStore {
    private const val PREFS = "plan_prefs"

    fun savePlan(context: Context, plan: Plan) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("name", plan.name)
            putInt("age", plan.age)
            putString("sex", plan.sex)
            putFloat("heightCm", plan.heightCm)
            putFloat("weightKg", plan.weightKg)
            putFloat("goalWeight", plan.goalWeight ?: 0f)
            putString("goal", plan.goal)
            putString("activityLevel", plan.activityLevel)
            putString("pace", plan.pace)
            putString("eatingStyle", plan.eatingStyle)
            putString("foodsToAvoid", plan.foodsToAvoid)
            putInt("dailyCalories", plan.dailyCalories)
            putInt("protein", plan.protein)
            putInt("carbs", plan.carbs)
            putInt("fat", plan.fat)
            putFloat("waterLiters", plan.waterLiters)
            putInt("weeksToGoal", plan.weekstoGoal ?: -1)
            apply()
        }
    }

    fun loadPlan(context: Context): Plan? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains("name")) return null
        return Plan(
            name = prefs.getString("name", "")!!,
            age = prefs.getInt("age", 30),
            sex = prefs.getString("sex", "Male")!!,
            heightCm = prefs.getFloat("heightCm", 170f),
            weightKg = prefs.getFloat("weightKg", 70f),
            goalWeight = prefs.getFloat("goalWeight", 0f).takeIf { it > 0f },
            goal = prefs.getString("goal", "maintain")!!,
            activityLevel = prefs.getString("activityLevel", "Lightly active")!!,
            pace = prefs.getString("pace", "Steady")!!,
            eatingStyle = prefs.getString("eatingStyle", "No preference")!!,
            foodsToAvoid = prefs.getString("foodsToAvoid", "")!!,
            dailyCalories = prefs.getInt("dailyCalories", 2000),
            protein = prefs.getInt("protein", 150),
            carbs = prefs.getInt("carbs", 200),
            fat = prefs.getInt("fat", 70),
            waterLiters = prefs.getFloat("waterLiters", 2.5f),
            weekstoGoal = prefs.getInt("weeksToGoal", -1).takeIf { it > 0 }
        )
    }
}