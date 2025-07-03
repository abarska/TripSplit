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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

    var expensePayerId by rememberSaveable { mutableLongStateOf(-1L) }
    val onPayerSelected: (Long) -> Unit = { expensePayerId = it }

    var selectedParticipants by rememberSaveable {
        mutableStateOf(setOf<TripParticipant>())
    }
    val onParticipantsSelected: (Set<TripParticipant>) -> Unit = { selectedParticipants = it }

    val onSaveExpense = {
        viewModel.saveExpense(
            expenseAmount = expenseAmount,
            expenseCurrencyCode = expenseCurrencyCode,
            selectedCategory = selectedCategory,
            selectedDate = selectedDate,
            expensePayerId = expensePayerId
        )
        navController.popBackStack()
    }

    LaunchedEffect(tripParticipants, tripCurrencies) {
        if (tripParticipants.isNotEmpty()) {
            if (selectedParticipants.isEmpty()) {
                selectedParticipants = tripParticipants.toSet()
            }
            if (expensePayerId == -1L) {
                expensePayerId = tripParticipants.first().id
            }
        }
        if (tripCurrencies.isNotEmpty() && expenseCurrencyCode.isEmpty()) {
            expenseCurrencyCode = tripCurrencies.first().code
        }
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
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange,
            expenseAmount = expenseAmount,
            expenseCurrencyCode = expenseCurrencyCode,
            tripCurrencies = tripCurrencies,
            onExpenseAmountChanged = onExpenseAmountChanged,
            onCurrencySelected = onCurrencySelected,
            tripParticipants = tripParticipants,
            expensePayerId = expensePayerId,
            onPayerSelected = onPayerSelected,
            selectedParticipants = selectedParticipants,
            onParticipantsSelected = onParticipantsSelected,
            onSaveExpense = onSaveExpense,
            modifier = modifier
        )
    } else {
        AddExpenseLandscapeContent(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange,
            expenseAmount = expenseAmount,
            expenseCurrencyCode = expenseCurrencyCode,
            tripCurrencies = tripCurrencies,
            onExpenseAmountChanged = onExpenseAmountChanged,
            onCurrencySelected = onCurrencySelected,
            tripParticipants = tripParticipants,
            expensePayerId = expensePayerId,
            onPayerSelected = onPayerSelected,
            selectedParticipants = selectedParticipants,
            onParticipantsSelected = onParticipantsSelected,
            onSaveExpense = onSaveExpense,
            modifier = modifier
        )
    }
}