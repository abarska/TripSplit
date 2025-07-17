package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.widgets.TsExpenseChartLegend
import com.anabars.tripsplit.ui.widgets.TsExpensePieChart

@Composable
fun PieChartLegendPortraitContent(categorizedExpenses: Map<ExpenseCategory, Double>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TsExpenseChartLegend(data = categorizedExpenses, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            TsExpensePieChart(data = categorizedExpenses)
        }
    }
}