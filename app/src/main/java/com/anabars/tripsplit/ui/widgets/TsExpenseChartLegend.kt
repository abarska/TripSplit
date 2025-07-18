package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakePieChartData
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import com.anabars.tripsplit.utils.getExpenseCategoryColors

@Composable
fun TsExpenseChartLegend(data: Map<ExpenseCategory, Double>, modifier: Modifier = Modifier) {
    val total = data.values.sum()
    val colors = getExpenseCategoryColors()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        data.entries.forEachIndexed { index, entry ->
            val percent = entry.value / total * 100
            val color = colors[index % colors.size]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color = color, shape = CircleShape)
                )
                TsInfoText(
                    text = stringResource(entry.key.titleRes) + ": " + "%.1f%%".format(percent),
                    maxLines = 1,
                    fontSize = TsFontSize.MEDIUM
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsExpenseChartLegendPreview() {
    TsExpenseChartLegend(
        data = getFakePieChartData(),
        modifier = Modifier.inputWidthModifier()
    )
}