package com.anabars.tripsplit.ui.screens.tripoverview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.utils.getFakeExpenseCategorizationResultMissingCurrencies
import com.anabars.tripsplit.ui.utils.getFakeExpenseCategorizationResultSuccess
import com.anabars.tripsplit.ui.utils.getFakeExpenseCategorizationResultInsufficientData
import com.anabars.tripsplit.ui.utils.getFakeExpenseCategorizationResultUnavailableData
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            is ExpenseCategorizationResult.UnavailableData ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    TsPlaceholderView(
                        painterRes = R.drawable.empty_pie_chart_image,
                        contentDescriptionRes = R.string.empty_pie_chart_image,
                        textRes = R.string.error_exchange_rates_unavailable
                    )
                }

            is ExpenseCategorizationResult.MissingCurrencies -> {
                val missingRates =
                    expenseCategorizationResult.missingCurrencies.joinToString(", ") { it }
                val text = String.format(
                    "%s: %s",
                    stringResource(R.string.error_exchange_rates_missing),
                    missingRates
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    TsPlaceholderView(
                        painterRes = R.drawable.empty_pie_chart_image,
                        contentDescriptionRes = R.string.empty_pie_chart_image,
                        text = text
                    )
                }
            }

            is ExpenseCategorizationResult.Success -> {
                if (expenseCategorizationResult.data.size < 3) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        TsPlaceholderView(
                            painterRes = R.drawable.empty_pie_chart_image,
                            contentDescriptionRes = R.string.empty_pie_chart_image,
                            textRes = R.string.placeholder_statistics,
                        )
                    }
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

@Preview
@Composable
private fun ExpenseStatisticsCardPreviewUnavailableData() {
    ExpenseStatisticsCard(
        expenseCategorizationResult = getFakeExpenseCategorizationResultUnavailableData(),
        isPortrait = true,
        modifier = Modifier.size(400.dp)
    )
}

@Preview
@Composable
private fun ExpenseStatisticsCardPreviewMissingCurrencies() {
    ExpenseStatisticsCard(
        expenseCategorizationResult = getFakeExpenseCategorizationResultMissingCurrencies(),
        isPortrait = true,
        modifier = Modifier.size(400.dp)
    )
}

@Preview
@Composable
private fun ExpenseStatisticsCardPreviewSuccessInsufficientData() {
    ExpenseStatisticsCard(
        expenseCategorizationResult = getFakeExpenseCategorizationResultInsufficientData(),
        isPortrait = true,
        modifier = Modifier.size(400.dp)
    )
}

@Preview
@Composable
private fun ExpenseStatisticsCardPreviewSuccessPortrait() {
    ExpenseStatisticsCard(
        expenseCategorizationResult = getFakeExpenseCategorizationResultSuccess(),
        isPortrait = true,
        modifier = Modifier
            .height(500.dp)
            .width(300.dp)
    )
}

@Preview
@Composable
private fun ExpenseStatisticsCardPreviewSuccessLandscape() {
    ExpenseStatisticsCard(
        expenseCategorizationResult = getFakeExpenseCategorizationResultSuccess(),
        isPortrait = false,
        modifier = Modifier
            .height(200.dp)
            .width(700.dp)
    )
}