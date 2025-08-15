package com.anabars.tripsplit.ui.screens.additem

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.AmountChanged
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.CategoryChanged
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.CurrencySelected
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.DateSelected
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.ParticipantsSelected
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.PayerSelected
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.SaveItem

@Composable
fun AddExpenseScreen(
    navController: NavHostController,
    onShowSnackbar: (Int) -> Unit,
    updateUpButtonAction: ((() -> Unit)?) -> Unit
) = AddItemScreenBase(
    navController,
    onShowSnackbar,
    updateUpButtonAction,
    portraitContent = { modifier, amountCurrency, payerParticipants, viewModel ->
        AddExpensePortraitContent(
            modifier = modifier,
            amountCurrencyState = amountCurrency,
            payerParticipantsState = payerParticipants,
            onDateSelected = { viewModel.onIntent(DateSelected(it)) },
            onCategoryChange = { viewModel.onIntent(CategoryChanged(it)) },
            onExpenseAmountChanged = { viewModel.onIntent(AmountChanged(it)) },
            onCurrencySelected = { viewModel.onIntent(CurrencySelected(it)) },
            onPayerSelected = { viewModel.onIntent(PayerSelected(it)) },
            onParticipantsSelected = { viewModel.onIntent(ParticipantsSelected(it)) },
            onSaveExpense = { viewModel.onIntent(SaveItem) }
        )
    },
    landscapeContent = { modifier, amountCurrency, payerParticipants, viewModel ->
        AddExpenseLandscapeContent(
            modifier = modifier,
            amountCurrencyState = amountCurrency,
            payerParticipantsState = payerParticipants,
            onDateSelected = { viewModel.onIntent(DateSelected(it)) },
            onCategoryChange = { viewModel.onIntent(CategoryChanged(it)) },
            onExpenseAmountChanged = { viewModel.onIntent(AmountChanged(it)) },
            onCurrencySelected = { viewModel.onIntent(CurrencySelected(it)) },
            onPayerSelected = { viewModel.onIntent(PayerSelected(it)) },
            onParticipantsSelected = { viewModel.onIntent(ParticipantsSelected(it)) },
            onSaveExpense = { viewModel.onIntent(SaveItem) }
        )
    }
)