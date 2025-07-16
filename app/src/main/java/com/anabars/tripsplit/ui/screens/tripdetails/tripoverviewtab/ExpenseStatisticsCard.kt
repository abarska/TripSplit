package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.widgets.TsExpenseChartLegend
import com.anabars.tripsplit.ui.widgets.TsExpensePieChart

@Composable
fun ExpenseStatisticsCard(
    exchangeRatesAvailable: Boolean,
    categorizedExpenses: Map<ExpenseCategory, Double>,
    modifier: Modifier = Modifier
) {
    TsContentCard(modifier = modifier) {
        when {
            !exchangeRatesAvailable ->
                TsInfoText(
                    textRes = R.string.placeholder_no_exchange_rates_available,
                    isHeader = true
                )

            categorizedExpenses.size < 3 ->
                TsInfoText(
                    textRes = R.string.placeholder_statistics,
                    isHeader = true
                )

            else ->
                Row (
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pie chart: 40% width, square shape
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.9f)
                            .aspectRatio(1f)
                            .weight(0.4f),
                        contentAlignment = Alignment.Center
                    ) {
                        TsExpensePieChart(data = categorizedExpenses)
                    }

                    // Legend: 60% width
                    Column(
                        modifier = Modifier
                            .weight(0.6f)
                            .padding(start = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TsExpenseChartLegend(data = categorizedExpenses)
                    }
                }
        }
    }
}