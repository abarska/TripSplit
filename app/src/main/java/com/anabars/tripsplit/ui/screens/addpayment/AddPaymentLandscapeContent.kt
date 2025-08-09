package com.anabars.tripsplit.ui.screens.addpayment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsMainButton
import com.anabars.tripsplit.ui.model.AddItemAmountCurrencyState
import com.anabars.tripsplit.ui.model.AddItemPayerParticipantsState
import com.anabars.tripsplit.ui.utils.getFakeAmountCurrencyUiState
import com.anabars.tripsplit.ui.utils.getFakePayerParticipantsState
import com.anabars.tripsplit.ui.widgets.TsDateAmountCurrencyCard
import com.anabars.tripsplit.ui.widgets.TsPaidByPaidForCard
import com.anabars.tripsplit.ui.widgets.UseCase
import java.time.LocalDate

@Composable
fun AddPaymentLandscapeContent(
    amountCurrencyState: AddItemAmountCurrencyState,
    payerParticipantsState: AddItemPayerParticipantsState,
    onDateSelected: (LocalDate) -> Unit,
    onExpenseAmountChanged: (String) -> Unit,
    onCurrencySelected: (String) -> Unit,
    onPayerSelected: (Long) -> Unit,
    onParticipantsSelected: (Set<TripParticipant>) -> Unit,
    modifier: Modifier = Modifier,
    onSavePayment: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
    ) {

        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            TsDateAmountCurrencyCard(
                useCase = UseCase.PAYMENT,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onDateSelected = onDateSelected,
                amountCurrencyState = amountCurrencyState,
                onExpenseAmountChanged = onExpenseAmountChanged,
                onCurrencySelected = onCurrencySelected
            )
            TsPaidByPaidForCard(
                useCase = UseCase.PAYMENT,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                payerParticipantsState = payerParticipantsState,
                onPayerSelected = onPayerSelected,
                onSelectionChanged = onParticipantsSelected
            )
        }

        TsMainButton(textRes = R.string.save) { onSavePayment() }
    }
}

@Preview (showBackground = true)
@Composable
private fun AddPaymentLandscapeContentPreview() {
    AddPaymentLandscapeContent(
        amountCurrencyState = getFakeAmountCurrencyUiState(),
        payerParticipantsState = getFakePayerParticipantsState(),
        onDateSelected = {},
        onExpenseAmountChanged = {},
        onCurrencySelected = {},
        onPayerSelected = {},
        onParticipantsSelected = {},
        onSavePayment = { }
    )
}