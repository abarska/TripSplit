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
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.viewmodels.AddExpenseViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel
import java.time.LocalDate

@Composable
fun AddExpenseScreen(navController: NavHostController, sharedViewModel: SharedViewModel) {

    val viewModel: AddExpenseViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val onDateSelected: (LocalDate) -> Unit = { viewModel.updateDate(it) }
    val onCategoryChanged: (ExpenseCategory) -> Unit = { viewModel.updateCategory(it) }
    val onExpenseAmountChanged: (String) -> Unit = { viewModel.updateExpenseAmount(it) }
    val onCurrencySelected: (String) -> Unit = { viewModel.updateCurrencyCode(it) }
    val onPayerSelected: (Long) -> Unit = { viewModel.updatePayerId(it) }
    val onParticipantsSelected: (Set<TripParticipant>) -> Unit = {
        viewModel.updateSelectedParticipants(it)
    }
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
            onDateSelected = onDateSelected,
            onCategoryChange = onCategoryChanged,
            onExpenseAmountChanged = onExpenseAmountChanged,
            onCurrencySelected = onCurrencySelected,
            onPayerSelected = onPayerSelected,
            onParticipantsSelected = onParticipantsSelected,
            onSaveExpense = onSaveExpense,
            modifier = modifier
        )
    } else {
        AddExpenseLandscapeContent(
            uiState = uiState,
            onDateSelected = onDateSelected,
            onCategoryChange = onCategoryChanged,
            onExpenseAmountChanged = onExpenseAmountChanged,
            onCurrencySelected = onCurrencySelected,
            onPayerSelected = onPayerSelected,
            onParticipantsSelected = onParticipantsSelected,
            onSaveExpense = onSaveExpense,
            modifier = modifier
        )
    }
}