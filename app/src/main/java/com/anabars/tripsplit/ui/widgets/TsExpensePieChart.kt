package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.utils.getExpenseCategoryColors
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun TsExpensePieChart(data: Map<ExpenseCategory, Double>) {
    val colors = getExpenseCategoryColors()
    val total = data.values.sum()
    if (total <= 0.0) return

    val sweepAngles = data.map { (_, value) -> (value / total * 360f).toFloat() }

    val lineColor = Color.Black
    val lineThickness = 2.dp

    Canvas(
        modifier = Modifier
            .fillMaxSize(0.8f)
            .aspectRatio(1f)
            .padding(top = 16.dp)
    ) {
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

        val startAngles = mutableListOf<Float>()

        data.entries.forEachIndexed { index, entry ->
            val sweep = sweepAngles[index]
            startAngles += currentStartAngle

            // Draw pie slices
            drawArc(
                color = colors[index % colors.size],
                startAngle = currentStartAngle,
                sweepAngle = sweep,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            currentStartAngle += sweep
        }

        // Draw the radial lines
        startAngles.forEach { angle ->
            val angleRad = Math.toRadians(angle.toDouble()).toFloat()
            val endX = center.x + radius * cos(angleRad)
            val endY = center.y + radius * sin(angleRad)
            drawLine(
                color = lineColor,
                start = center,
                end = Offset(endX, endY),
                strokeWidth = lineThicknessPx
            )
        }
    }
}
