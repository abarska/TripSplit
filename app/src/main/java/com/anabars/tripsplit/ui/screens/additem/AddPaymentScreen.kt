package com.anabars.tripsplit.ui.screens.additem

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.AmountChanged
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.CurrencySelected
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.DateSelected
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.ParticipantsSelected
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.PayerSelected
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent.SaveItem

@Composable
fun AddPaymentScreen(
    navController: NavHostController,
    onShowSnackbar: (Int) -> Unit,
    updateUpButtonAction: ((() -> Unit)?) -> Unit
) = AddItemScreenBase(
    navController,
    onShowSnackbar,
    updateUpButtonAction,
    portraitContent = { modifier, amountCurrency, payerParticipants, vm ->
        AddPaymentPortraitContent(
            modifier = modifier,
            amountCurrencyState = amountCurrency,
            payerParticipantsState = payerParticipants,
            onDateSelected = { vm.onIntent(DateSelected(it)) },
            onExpenseAmountChanged = { vm.onIntent(AmountChanged(it)) },
            onCurrencySelected = { vm.onIntent(CurrencySelected(it)) },
            onPayerSelected = { vm.onIntent(PayerSelected(it)) },
            onParticipantsSelected = { vm.onIntent(ParticipantsSelected(it)) },
            onSavePayment = { vm.onIntent(SaveItem) }
        )
    },
    landscapeContent = { modifier, amountCurrency, payerParticipants, vm ->
        AddPaymentLandscapeContent(
            modifier = modifier,
            amountCurrencyState = amountCurrency,
            payerParticipantsState = payerParticipants,
            onDateSelected = { vm.onIntent(DateSelected(it)) },
            onExpenseAmountChanged = { vm.onIntent(AmountChanged(it)) },
            onCurrencySelected = { vm.onIntent(CurrencySelected(it)) },
            onPayerSelected = { vm.onIntent(PayerSelected(it)) },
            onParticipantsSelected = { vm.onIntent(ParticipantsSelected(it)) },
            onSavePayment = { vm.onIntent(SaveItem) }
        )
    }
)