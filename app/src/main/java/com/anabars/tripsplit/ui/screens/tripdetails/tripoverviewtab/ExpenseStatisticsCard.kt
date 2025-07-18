package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.widgets.TsPlaceholderView
import com.anabars.tripsplit.viewmodels.ExpenseCategorizationResult

@Composable
fun ExpenseStatisticsCard(
    expenseCategorizationResult: ExpenseCategorizationResult,
    isPortrait: Boolean,
    modifier: Modifier = Modifier
) {
    TsContentCard(modifier = modifier) {

        when (expenseCategorizationResult) {
            is ExpenseCategorizationResult.Loading ->
                CircularProgressIndicator()

            is ExpenseCategorizationResult.UnavailableData ->
                TsPlaceholderView(
                    painterRes = R.drawable.empty_pie_chart_image,
                    contentDescriptionRes = R.string.empty_pie_chart_image,
                    textRes = R.string.error_exchange_rates_unavailable
                )

            is ExpenseCategorizationResult.MissingCurrencies -> {
                val missingRates =
                    expenseCategorizationResult.missingCurrencies.joinToString(", ") { it }
                val text = String.format(
                    "%s: %s",
                    stringResource(R.string.error_exchange_rates_missing),
                    missingRates
                )
                TsPlaceholderView(
                    painterRes = R.drawable.empty_pie_chart_image,
                    contentDescriptionRes = R.string.empty_pie_chart_image,
                    text = text
                )
            }

            is ExpenseCategorizationResult.Success -> {
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