package com.example.calorietracker.ui.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietracker.data.FoodLog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    plan: com.example.calorietracker.data.Plan,
    foodLogs: List<FoodLog>,
    waterMl: Int,
    onAddWater: (Int) -> Unit,
    onLogClick: () -> Unit,
    onRemoveFood: (FoodLog) -> Unit,
    onResetDay: () -> Unit
) {
    val today = LocalDate.now()
    val todayLogs = foodLogs.filter { it.date == today }
    val totalCalories = todayLogs.sumOf { it.calories }
    val totalProtein = todayLogs.sumOf { it.protein.toDouble() }
    val totalCarbs = todayLogs.sumOf { it.carbs.toDouble() }
    val totalFat = todayLogs.sumOf { it.fat.toDouble() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Today",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        today.format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = onLogClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Log Food")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Food")
                }
            }
        }

        // Calorie Ring
        item {
            CalorieRingCard(
                consumed = totalCalories,
                target = plan.dailyCalories
            )
        }

        // Macro Rings
        item {
            MacroRingsCard(
                protein = totalProtein.toFloat(),
                proteinTarget = plan.protein.toFloat(),
                carbs = totalCarbs.toFloat(),
                carbsTarget = plan.carbs.toFloat(),
                fat = totalFat.toFloat(),
                fatTarget = plan.fat.toFloat()
            )
        }

        // Water
        item {
            WaterCard(
                currentMl = waterMl,
                targetLiters = plan.waterLiters,
                onAdd = { onAddWater(250) },
                onSubtract = { onAddWater(-250) }
            )
        }

        // Food Log
        if (todayLogs.isNotEmpty()) {
            item {
                Text(
                    "Today's Meals",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(todayLogs) { log ->
                FoodLogItem(log = log, onRemove = { onRemoveFood(log) })
            }

            item {
                TextButton(
                    onClick = onResetDay,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Today's Log", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // 7-Day History
        item {
            SevenDayHistoryCard(logs = foodLogs, target = plan.dailyCalories)
        }

        // Backup/Restore
        item {
            BackupCard(foodLogs = foodLogs, waterMl = waterMl)
        }
    }
}

@Composable
fun CalorieRingCard(consumed: Int, target: Int) {
    val progress = if (target > 0) (consumed.toFloat() / target).coerceIn(0f, 1f) else 0f
    val remaining = maxOf(target - consumed, 0)
    val progressAnim = animateFloatAsState(targetValue = progress, animationSpec = tween(600))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                // Background ring
                drawArc(
                    color = Color(0xFFE0E7FF),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                )
                // Progress ring
                drawArc(
                    color = Color(0xFF2563EB),
                    startAngle = -90f,
                    sweepAngle = progressAnim.value * 360f,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "$consumed",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "of $target cal",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "$remaining left",
                    fontSize = 14.sp,
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MacroRingsCard(
    protein: Float, proteinTarget: Float,
    carbs: Float, carbsTarget: Float,
    fat: Float, fatTarget: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MacroRing(
                label = "Protein",
                consumed = protein,
                target = proteinTarget,
                color = Color(0xFF2563EB)
            )
            MacroRing(
                label = "Carbs",
                consumed = carbs,
                target = carbsTarget,
                color = Color(0xFF14B8A6)
            )
            MacroRing(
                label = "Fat",
                consumed = fat,
                target = fatTarget,
                color = Color(0xFFF59E0B)
            )
        }
    }
}

@Composable
fun MacroRing(label: String, consumed: Float, target: Float, color: Color) {
    val progress = if (target > 0) (consumed / target).coerceIn(0f, 1f) else 0f
    val progressAnim = animateFloatAsState(targetValue = progress, animationSpec = tween(600))

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(80.dp)) {
                drawArc(
                    color = color.copy(alpha = 0.2f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = progressAnim.value * 360f,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Text(
                "${consumed.toInt()}/${target.toInt()}g",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun WaterCard(currentMl: Int, targetLiters: Float, onAdd: () -> Unit, onSubtract: () -> Unit) {
    val currentLiters = currentMl / 1000f
    val progress = if (targetLiters > 0) (currentLiters / targetLiters).coerceIn(0f, 1f) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocalDrink,
                contentDescription = "Water",
                tint = Color(0xFF3B82F6),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Water",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "%.1f / %.1fL".format(currentLiters, targetLiters),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color(0xFF3B82F6),
                    trackColor = Color(0xFFDBEAFE)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Row {
                IconButton(onClick = onSubtract) {
                    Icon(Icons.Default.Remove, contentDescription = "Subtract water")
                }
                IconButton(onClick = onAdd) {
                    Icon(Icons.Default.Add, contentDescription = "Add water")
                }
            }
        }
    }
}

@Composable
fun FoodLogItem(log: FoodLog, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    log.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "${log.serving} • ${log.meal}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    Text("${log.calories} cal", fontSize = 11.sp, color = Color(0xFF2563EB))
                    Text(" • ${log.protein.toInt()}P ${log.carbs.toInt()}C ${log.fat.toInt()}F", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SevenDayHistoryCard(logs: List<FoodLog>, target: Int) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "7-Day History",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                val barWidth = size.width / (days.size * 2)
                val maxCal = target * 1.5f

                days.forEachIndexed { index, date ->
                    val dayCal = logs.filter { it.date == date }.sumOf { it.calories }.toFloat()
                    val barHeight = (dayCal / maxCal) * size.height
                    val x = (index * 2 + 0.5f) * barWidth

                    drawRect(
                        color = if (dayCal > target) Color(0xFFEF4444) else Color(0xFF2563EB),
                        topLeft = androidx.compose.ui.geometry.Offset(x, size.height - barHeight),
                        size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                days.forEach { date ->
                    Text(
                        date.format(DateTimeFormatter.ofPattern("EEE")),
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            if (target > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF2563EB), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Below target", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFEF4444), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Above target", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun BackupCard(foodLogs: List<FoodLog>, waterMl: Int) {
    var expanded by remember { mutableStateOf(false) }
    var savedCode by remember { mutableStateOf("") }
    var restoreCode by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Backup",
                    tint = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Backup & Restore",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))

                // Generate save code
                Button(
                    onClick = {
                        // Simple JSON-like backup
                        val logData = foodLogs.joinToString(";") {
                            "${it.date}|${it.meal}|${it.name}|${it.serving}|${it.calories}|${it.protein}|${it.carbs}|${it.fat}"
                        }
                        savedCode = "WATER:${waterMl}|LOGS:${logData}"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy Save Code")
                }

                if (savedCode.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            savedCode.take(100) + if (savedCode.length > 100) "..." else "",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = restoreCode,
                    onValueChange = { restoreCode = it },
                    label = { Text("Paste save code to restore") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { /* TODO: parse and restore */ },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = restoreCode.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text("Restore")
                }
            }
        }
    }
}