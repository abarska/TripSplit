package com.anabars.tripsplit.ui.screens.tripdetails.addexpense

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.viewmodels.AddExpenseViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel

@Composable
fun AddExpenseScreen(
    tripId: Long,
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {

    val viewModel: AddExpenseViewModel = hiltViewModel()

    BackHandler {
        if (!sharedViewModel.handleBack()) {
            navController.popBackStack()
        }
    }
    InfoText(text = "add a new expense for ${viewModel.tripId}")
}