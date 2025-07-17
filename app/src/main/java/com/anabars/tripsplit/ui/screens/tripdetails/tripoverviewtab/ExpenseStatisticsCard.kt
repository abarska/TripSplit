package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.widgets.TsPlaceholderView
import com.anabars.tripsplit.viewmodels.ExpenseCategorizationResult

@Composable
fun ExpenseStatisticsCard(
    exchangeRatesAvailable: Boolean,
    expenseCategorizationResult: ExpenseCategorizationResult,
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

            expenseCategorizationResult is ExpenseCategorizationResult.Error ->
                TsPlaceholderView(
                    painterRes = R.drawable.empty_pie_chart_image,
                    contentDescriptionRes = R.string.empty_pie_chart_image,
                    textRes = R.string.error_missing_exchange_rates
                )

            expenseCategorizationResult is ExpenseCategorizationResult.Success -> {

                if (expenseCategorizationResult.data.size < 3) {
                    TsPlaceholderView(
                        painterRes = R.drawable.empty_pie_chart_image,
                        contentDescriptionRes = R.string.empty_pie_chart_image,
                        textRes = R.string.placeholder_statistics,
                    )
                } else {
                    if (isPortrait)
                        PieChartLegendPortraitContent(categorizedExpenses = expenseCategorizationResult.data)
                    else
                        PieChartLegendLandscapeContent(categorizedExpenses = expenseCategorizationResult.data)
                }
            }
        }
    }
}