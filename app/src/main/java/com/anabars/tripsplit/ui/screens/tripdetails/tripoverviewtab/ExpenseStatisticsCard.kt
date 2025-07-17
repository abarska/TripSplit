package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.widgets.TsPlaceholderView

@Composable
fun ExpenseStatisticsCard(
    exchangeRatesAvailable: Boolean,
    categorizedExpenses: Map<ExpenseCategory, Double>,
    isPortrait: Boolean,
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
                TsPlaceholderView(
                    painterRes = R.drawable.empty_pie_chart_image,
                    contentDescriptionRes = R.string.empty_pie_chart_image,
                    textRes = R.string.placeholder_statistics,
                )

            else ->
                if (isPortrait) {
                    PieChartLegendPortraitContent(categorizedExpenses = categorizedExpenses)
                } else {
                    PieChartLegendLandscapeContent(categorizedExpenses = categorizedExpenses)
                }
        }
    }
}