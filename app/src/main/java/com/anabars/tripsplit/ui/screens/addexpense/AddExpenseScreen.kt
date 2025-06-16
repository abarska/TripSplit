package com.anabars.tripsplit.ui.screens.addexpense

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsMainButton
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.viewmodels.AddExpenseViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel
import java.time.LocalDate

@Composable
fun AddExpenseScreen(navController: NavHostController, sharedViewModel: SharedViewModel) {

    val viewModel: AddExpenseViewModel = hiltViewModel()
    val tripCurrencies by viewModel.tripCurrencies.collectAsState()
    val tripParticipants by viewModel.tripParticipants.collectAsState()

    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val onDateSelected: (LocalDate) -> Unit = { selectedDate = it }

    var selectedCategory by rememberSaveable(stateSaver = ExpenseCategory.expenseCategorySaver) {
        mutableStateOf(ExpenseCategory.Miscellaneous)
    }
    val onCategoryChange: (ExpenseCategory) -> Unit = { newCategory ->
        selectedCategory = newCategory
    }

    var expenseAmount by rememberSaveable { mutableStateOf("") }
    val onExpenseAmountChanged: (String) -> Unit = { expenseAmount = it }

    var expenseCurrencyCode by rememberSaveable { mutableStateOf("") }
    val onCurrencySelected: (String) -> Unit = { expenseCurrencyCode = it }

    val you = stringResource(R.string.you)
    var expensePayer by rememberSaveable { mutableStateOf(you) }
    val onPayerSelected: (String) -> Unit = { expensePayer = it }

    var selectedParticipants by rememberSaveable {
        mutableStateOf(setOf<TripParticipant>())
    }
    val onParticipantsSelected: (Set<TripParticipant>) -> Unit = { selectedParticipants = it }

    val onSaveExpense = {
        Log.d("marysya", "AddExpenseScreen: save")
    }

    LaunchedEffect(tripParticipants) {
        if (selectedParticipants.isEmpty() && tripParticipants.isNotEmpty()) {
            selectedParticipants = tripParticipants.toSet()
        }
        if (expenseCurrencyCode.isEmpty() && tripCurrencies.isNotEmpty()) {
            expenseCurrencyCode = tripCurrencies.first().code
        }
    }

    BackHandler {
        if (!sharedViewModel.handleBack()) {
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
    ) {

        ExpenseDateAndCategoryCard(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange
        )

        ExpenseAmountAndCurrencyCard(
            expenseAmount = expenseAmount,
            expenseCurrencyCode = expenseCurrencyCode,
            tripCurrencies = tripCurrencies,
            onExpenseAmountChanged = onExpenseAmountChanged,
            onCurrencySelected = onCurrencySelected
        )

        ExpensePaidByAndPaidForCard(
            tripParticipants = tripParticipants,
            expensePayer = expensePayer,
            onPayerSelected = onPayerSelected,
            selectedParticipants = selectedParticipants,
            onSelectionChanged = onParticipantsSelected
        )

        TsMainButton(textRes = R.string.save) { onSaveExpense() }
    }
}

