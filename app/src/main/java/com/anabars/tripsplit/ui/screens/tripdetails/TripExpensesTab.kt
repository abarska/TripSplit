package com.anabars.tripsplit.ui.screens.tripdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.TripSplitFab
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.viewmodels.TripExpensesViewModel

@Composable
fun TripExpensesTab(
    navController: NavController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {

    val viewModel: TripExpensesViewModel = hiltViewModel()
    val expenses by viewModel.tripExpenses.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(expenses) { expense ->
                InfoText(
                    text = "${stringResource(expense.category.titleRes)}: ${expense.amount} ${expense.currency}",
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
        }
        TripSplitFab(
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