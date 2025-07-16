package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.widgets.TsExpensePieChart

@Composable
fun ExpenseStatisticsCard(
    categorizedExpenses: Map<ExpenseCategory, Double>,
    modifier: Modifier = Modifier
) {
    TsContentCard(modifier = modifier) {
        if (categorizedExpenses.size < 3) {
            TsInfoText(textRes = R.string.placeholder_statistics, isHeader = true)
        } else {
            TsExpensePieChart(
                data = categorizedExpenses,
                modifier = Modifier
                    .fillMaxSize(0.6f)
                    .aspectRatio(1f)
                    .padding(top = 16.dp)
            )
        }
    }
}