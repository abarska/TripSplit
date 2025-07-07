package com.anabars.tripsplit.ui.screens.addexpense

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anabars.tripsplit.ui.model.AddExpenseEvent
import com.anabars.tripsplit.viewmodels.AddExpenseViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel

@Composable
fun AddExpenseScreen(navController: NavHostController, sharedViewModel: SharedViewModel) {

    val viewModel: AddExpenseViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsState()
    val dateCategoryState by viewModel.dateCategoryState.collectAsState()
    val amountCurrencyState by viewModel.amountCurrencyState.collectAsState()

    val onSaveExpense = {
        viewModel.saveExpense()
        navController.popBackStack()
    }

    BackHandler {
        if (!sharedViewModel.handleBack()) {
            navController.popBackStack()
        }
    }

    val isPortrait =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val scrollState = rememberScrollState()
    val modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(16.dp)

    if (isPortrait) {
        AddExpensePortraitContent(
            uiState = uiState,
            dateCategoryState = dateCategoryState,
            amountCurrencyState = amountCurrencyState,
            onDateSelected = { viewModel.onEvent(AddExpenseEvent.DateSelected(it)) },
            onCategoryChange = { viewModel.onEvent(AddExpenseEvent.CategoryChanged(it)) },
            onExpenseAmountChanged = { viewModel.onEvent(AddExpenseEvent.AmountChanged(it)) },
            onCurrencySelected = { viewModel.onEvent(AddExpenseEvent.CurrencySelected(it)) },
            onPayerSelected = { viewModel.onEvent(AddExpenseEvent.PayerSelected(it)) },
            onParticipantsSelected = { viewModel.onEvent(AddExpenseEvent.ParticipantsSelected(it)) },
            onSaveExpense = onSaveExpense,
            modifier = modifier
        )
    } else {
        AddExpenseLandscapeContent(
            uiState = uiState,
            dateCategoryState = dateCategoryState,
            amountCurrencyState = amountCurrencyState,
            onDateSelected = { viewModel.onEvent(AddExpenseEvent.DateSelected(it)) },
            onCategoryChange = { viewModel.onEvent(AddExpenseEvent.CategoryChanged(it)) },
            onExpenseAmountChanged = { viewModel.onEvent(AddExpenseEvent.AmountChanged(it)) },
            onCurrencySelected = { viewModel.onEvent(AddExpenseEvent.CurrencySelected(it)) },
            onPayerSelected = { viewModel.onEvent(AddExpenseEvent.PayerSelected(it)) },
            onParticipantsSelected = { viewModel.onEvent(AddExpenseEvent.ParticipantsSelected(it)) },
            onSaveExpense = onSaveExpense,
            modifier = modifier
        )
    }
}