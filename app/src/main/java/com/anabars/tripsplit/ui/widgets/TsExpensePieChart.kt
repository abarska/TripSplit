package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.model.ExpenseCategory
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

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

    val lineColor = MaterialTheme.colorScheme.onSurface
    val lineThickness = 4.dp

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) / 2f
        val lineThicknessPx = lineThickness.toPx()
        var currentStartAngle = 0f

        // Draw the outer stroke around the entire pie chart
        drawCircle(
            color = lineColor,
            center = center,
            radius = radius,
            style = Stroke(width = lineThicknessPx)
        )

        data.entries.forEachIndexed { index, entry ->
            val sweep = sweepAngles[index]
            // Draw each slice
            drawArc(
                color = colors[index % colors.size],
                startAngle = currentStartAngle,
                sweepAngle = sweep,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            // Draw radial lines
            val endAngle = currentStartAngle + sweep
            val endAngleRad = Math.toRadians(endAngle.toDouble()).toFloat()
            val lineEndX = center.x + radius * cos(endAngleRad)
            val lineEndY = center.y + radius * sin(endAngleRad)
            drawLine(
                color = lineColor,
                start = center,
                end = Offset(lineEndX, lineEndY),
                strokeWidth = lineThicknessPx
            )
            currentStartAngle += sweep
        }
    }
}
