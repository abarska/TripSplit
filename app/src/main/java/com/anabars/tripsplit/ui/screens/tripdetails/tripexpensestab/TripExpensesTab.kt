package com.anabars.tripsplit.ui.screens.tripdetails.tripexpensestab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsFab
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.ui.widgets.TsPlaceholderView
import com.anabars.tripsplit.viewmodels.GroupedExpensesResult
import com.anabars.tripsplit.viewmodels.TripExpensesViewModel

@Composable
fun TripExpensesTab(
    navController: NavController,
    onTabTitleChange: (String) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {

    val viewModel: TripExpensesViewModel = hiltViewModel()
    val groupedExpensesResult by viewModel.groupedExpensesResult.collectAsState()
    val tripParticipants by viewModel.tripParticipants.collectAsState()

    val screenTitle = String.format(
        "%s: %s",
        stringResource(R.string.title_trip_details),
        stringResource(R.string.title_tab_expenses)
    )
    LaunchedEffect(Unit) {
        onTabTitleChange(screenTitle)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        when (groupedExpensesResult) {
            is GroupedExpensesResult.Loading ->
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            is GroupedExpensesResult.Empty ->
                TsPlaceholderView(
                    painterRes = R.drawable.empty_wallet_image,
                    contentDescriptionRes = R.string.empty_wallet_image,
                    textRes = R.string.placeholder_expenses
                )

            is GroupedExpensesResult.Success ->
                TripExpensesData(
                    groupedExpensesResult = groupedExpensesResult,
                    tripParticipants = tripParticipants,
                    onDeleteClick = {expenseId -> viewModel.deleteExpenseById(expenseId) }
                )
        }

        TsFab(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            iconVector = Icons.Outlined.Add,
            contentDescription = R.string.add_a_new_expense,
        ) {
            navController.navigate("${AppScreens.ROUTE_ADD_EXPENSE}/${viewModel.tripId}")
        }
    }
}