package com.anabars.tripsplit.ui.screens.addpayment

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.viewmodels.AddItemViewModel

@Composable
fun AddPaymentScreen(
    navController: NavHostController,
    onTabTitleChange: (String) -> Unit,
    setBackHandler: ((() -> Boolean)?) -> Unit
) {

    val viewModel: AddItemViewModel = hiltViewModel()

    val screenTitle = stringResource(R.string.title_new_payment)
    LaunchedEffect(Unit) {
        onTabTitleChange(screenTitle)
    }

    val isPortrait =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val scrollState = rememberScrollState()
    val modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(16.dp)
//    if (isPortrait) {
//        AddPaymentPortraitContent(
//            modifier = modifier,
//            amountCurrencyState = TODO(),
//            payerParticipantsState = TODO(),
//            onDateSelected = TODO(),
//            onExpenseAmountChanged = TODO(),
//            onCurrencySelected = TODO(),
//            onPayerSelected = TODO(),
//            onParticipantsSelected = TODO(),
//            onSavePayment = TODO()
//        )
//    } else {
//        AddPaymentLandscapeContent(
//            modifier = modifier,
//            amountCurrencyState = TODO(),
//            payerParticipantsState = TODO(),
//            onDateSelected = TODO(),
//            onExpenseAmountChanged = TODO(),
//            onCurrencySelected = TODO(),
//            onPayerSelected = TODO(),
//            onParticipantsSelected = TODO(),
//            onSavePayment = TODO()
//        )
//    }
}