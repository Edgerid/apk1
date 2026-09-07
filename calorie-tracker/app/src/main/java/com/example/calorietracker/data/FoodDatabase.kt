package com.example.calorietracker.data

object FoodDatabase {
    data class Food(
        val name: String,
        val serving: String,
        val calories: Int,
        val protein: Float,
        val carbs: Float,
        val fat: Float
    )

    private val foods = listOf(
        Food("Egg (1 large)", "1 egg", 70, 6f, 0f, 5f),
        Food("Egg white", "1 large", 17, 4f, 0f, 0f),
        Food("Chicken breast", "100g", 165, 31f, 0f, 3.6f),
        Food("Chicken thigh", "100g", 209, 26f, 0f, 10.9f),
        Food("Ground beef (lean)", "100g", 250, 26f, 0f, 15f),
        Food("Salmon fillet", "100g", 208, 20f, 0f, 13f),
        Food("Tuna (canned)", "100g", 128, 26f, 0f, 1f),
        Food("Shrimp", "100g", 99, 24f, 0.2f, 0.3f),
        Food("Tofu", "100g", 76, 8f, 1.9f, 4.8f),
        Food("Black beans", "100g cooked", 132, 8.9f, 23.7f, 0.5f),
        Food("Lentils", "100g cooked", 116, 9f, 20f, 0.4f),
        Food("Brown rice", "100g cooked", 123, 2.7f, 25.6f, 1f),
        Food("White rice", "100g cooked", 130, 2.7f, 28.2f, 0.3f),
        Food("Oatmeal", "100g cooked", 68, 2.4f, 12f, 1.4f),
        Food("Oatmeal (dry)", "40g", 152, 5.4f, 27f, 2.6f),
        Food("Pasta (cooked)", "100g", 131, 5f, 25f, 1.1f),
        Food("Bread (whole wheat)", "1 slice", 82, 4f, 14f, 1.2f),
        Food("Bread (white)", "1 slice", 74, 2.5f, 14f, 1f),
        Food("Tortilla (flour)", "1 medium", 139, 3.6f, 24f, 3.6f),
        Food("Banana", "1 medium", 105, 1.3f, 27f, 0.4f),
        Food("Apple", "1 medium", 95, 0.5f, 25f, 0.3f),
        Food("Orange", "1 medium", 62, 1.2f, 15f, 0.2f),
        Food("Strawberries", "100g", 32, 0.7f, 7.7f, 0.3f),
        Food("Blueberries", "100g", 57, 0.7f, 14f, 0.3f),
        Food("Avocado", "1 medium", 240, 3f, 13f, 22f),
        Food("Potato (baked)", "1 medium", 161, 4.3f, 37f, 0.2f),
        Food("Sweet potato", "1 medium", 103, 2.3f, 24f, 0.1f),
        Food("Broccoli", "100g", 34, 2.8f, 7f, 0.4f),
        Food("Spinach", "100g", 23, 2.9f, 3.6f, 0.4f),
        Food("Carrots", "100g", 41, 0.9f, 10f, 0.2f),
        Food("Cucumber", "100g", 16, 0.7f, 3.6f, 0.1f),
        Food("Tomato", "1 medium", 22, 1f, 4.8f, 0.2f),
        Food("Lettuce", "100g", 15, 1.4f, 2.9f, 0.2f),
        Food("Olive oil", "1 tbsp", 119, 0f, 0f, 14f),
        Food("Butter", "1 tbsp", 102, 0.1f, 0f, 11.5f),
        Food("Cheese (cheddar)", "28g (1oz)", 113, 7f, 0.4f, 9.3f),
        Food("Milk (whole)", "1 cup (240ml)", 149, 8f, 12f, 8f),
        Food("Milk (skim)", "1 cup", 86, 8f, 12f, 0.2f),
        Food("Greek yogurt", "100g", 59, 10f, 3.6f, 0.7f),
        Food("Almonds", "28g", 164, 6f, 6f, 14f),
        Food("Peanut butter", "2 tbsp", 196, 7f, 7f, 16f),
        Food("Honey", "1 tbsp", 64, 0.1f, 17f, 0f),
        Food("Cereal (cornflakes)", "30g", 112, 2f, 26f, 0.1f),
        Food("Protein powder", "1 scoop (30g)", 120, 24f, 3f, 1.5f),
        Food("Coffee (black)", "1 cup", 2, 0.3f, 0f, 0f),
        Food("Orange juice", "1 cup", 112, 1.7f, 26f, 0.5f),
        Food("Granola bar", "1 bar (40g)", 180, 3f, 30f, 6f),
        Food("Pizza slice", "1 large slice", 285, 12f, 36f, 10f),
        Food("Hamburger", "1 patty + bun", 354, 20f, 31f, 16f),
        Food("French fries", "100g", 312, 3.4f, 41f, 15f),
        Food("Taco", "1 medium", 226, 9f, 20f, 12f),
        Food("Burrito", "1 large", 380, 18f, 44f, 13f),
        Food("Caesar salad", "1 serving", 180, 8f, 7f, 14f),
        Food("Sushi (California roll)", "8 pieces", 255, 9f, 38f, 7f),
        Food("Fried chicken", "100g", 260, 16f, 10f, 18f),
        Food("Ice cream", "100g", 207, 3.5f, 24f, 11f),
        Food("Dark chocolate", "30g", 155, 2f, 13f, 11f),
        Food("Beer", "1 can (355ml)", 154, 1.3f, 13f, 0f),
        Food("Wine", "1 glass (150ml)", 125, 0.1f, 4f, 0f),
        Food("Coca-Cola", "1 can (355ml)", 140, 0f, 39f, 0f)
    )

    fun search(query: String): List<Food> {
        val q = query.lowercase().trim()
        return foods.filter { it.name.lowercase().contains(q) }.take(5)
    }

    fun getAll(): List<Food> = foods
}