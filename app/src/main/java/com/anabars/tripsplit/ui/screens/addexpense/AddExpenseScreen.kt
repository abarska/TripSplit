package com.anabars.tripsplit.ui.screens.addexpense

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    val dateCategoryState by viewModel.dateCategoryState.collectAsState()
    val amountCurrencyState by viewModel.amountCurrencyState.collectAsState()
    val payerParticipantsState by viewModel.payerParticipantsState.collectAsState()
    val addExpenseErrorState by viewModel.addExpenseErrorState.collectAsState()
    val navigateBackState by viewModel.navigateBackState.collectAsState()

    val onSaveExpense = {
        viewModel.saveExpense()
    }

    BackHandler {
        if (!sharedViewModel.handleBack()) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(navigateBackState) {
        if (navigateBackState) {
            navController.popBackStack()
            viewModel.onNavigatedBack()
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
            addExpenseErrorState = addExpenseErrorState,
            dateCategoryState = dateCategoryState,
            amountCurrencyState = amountCurrencyState,
            payerParticipantsState = payerParticipantsState,
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
            addExpenseErrorState = addExpenseErrorState,
            dateCategoryState = dateCategoryState,
            amountCurrencyState = amountCurrencyState,
            payerParticipantsState = payerParticipantsState,
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