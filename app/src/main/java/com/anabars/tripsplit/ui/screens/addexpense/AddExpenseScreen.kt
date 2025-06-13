package com.anabars.tripsplit.ui.screens.addexpense

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.widgets.DateInputSection
import com.anabars.tripsplit.utils.getDefaultCurrency
import com.anabars.tripsplit.viewmodels.AddExpenseViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel
import java.time.LocalDate

@Composable
fun AddExpenseScreen(
    tripId: Long,
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {

    val viewModel: AddExpenseViewModel = hiltViewModel()
    val tripCurrencies by viewModel.tripCurrencies.collectAsState()
    val tripParticipants by viewModel.tripParticipants.collectAsState()

    var selectedCategory by rememberSaveable(stateSaver = ExpenseCategory.expenseCategorySaver) {
        mutableStateOf(ExpenseCategory.Miscellaneous)
    }
    val onCategoryChange: (ExpenseCategory) -> Unit = { newCategory ->
        selectedCategory = newCategory
    }

    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var expenseAmount by rememberSaveable { mutableStateOf("") }
    var expenseCurrencyCode by rememberSaveable { mutableStateOf(getDefaultCurrency()) }
    val you = stringResource(R.string.you)
    var expensePayer by rememberSaveable { mutableStateOf(you) }

    BackHandler {
        if (!sharedViewModel.handleBack()) {
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        DateInputSection(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))

        ExpenseCategoriesRadioGroup(
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))

        ExpenseAmountInputField(
            value = expenseAmount,
            onValueChange = { expenseAmount = it },
            currencyPrefix = expenseCurrencyCode,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))

        InfoText(textRes = R.string.currency)

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_small)))

        ExpenseCurrenciesRadioGroup(
            modifier = Modifier.padding(horizontal = 16.dp),
            currencies = tripCurrencies,
            expenseCurrencyCode = expenseCurrencyCode,
            onCurrencySelected = { expenseCurrencyCode = it },
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))

        InfoText(textRes = R.string.expense_paid_by)

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_small)))

        ExpensePayerRadioGroup(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            participants = tripParticipants,
            paidBy = expensePayer,
            onPayerSelected = { expensePayer = it },
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))

        InfoText(textRes = R.string.expense_paid_for)

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_small)))
    }
}

