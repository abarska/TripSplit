package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            TsExpensePieChart(data = categorizedExpenses)
        }
    }
}