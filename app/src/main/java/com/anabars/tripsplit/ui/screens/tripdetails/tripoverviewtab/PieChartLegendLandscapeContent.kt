package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.widgets.TsExpenseChartLegend
import com.anabars.tripsplit.ui.widgets.TsExpensePieChart

@Composable
fun PieChartLegendLandscapeContent(categorizedExpenses: Map<ExpenseCategory, Double>) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TsExpenseChartLegend(data = categorizedExpenses)
        Spacer(Modifier.width(32.dp))
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            TsExpensePieChart(data = categorizedExpenses)
        }
    }
}