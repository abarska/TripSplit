package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.anabars.tripsplit.ui.model.ExpenseCategory

@Composable
fun TsExpensePieChart(data: Map<ExpenseCategory, Double>, modifier: Modifier = Modifier) {
    val colors = listOf(
        Color(0xFFFF69B4),
        Color(0xFF3CB371),
        Color(0xFF8A2BE2),
        Color(0xFF00BFFF),
        Color(0xFFFFA500)

    )
    val total = data.values.sum()
    if (total <= 0.0) return

    val sweepAngles = data.map { (_, value) -> (value / total * 360f).toFloat() }

    Canvas(modifier = modifier) {
        var startAngle = 0f
        data.entries.forEachIndexed { index, entry ->
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngles[index],
                useCenter = true
            )
            startAngle += sweepAngles[index]
        }
    }
}