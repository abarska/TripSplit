package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.model.AddItemPayerParticipantsState
import com.anabars.tripsplit.ui.utils.getFakeAddExpensePayerParticipantsState
import com.anabars.tripsplit.viewmodels.AddItemViewModel

@Composable
fun TsPaidByPaidForCard(
    useCase: AddItemViewModel.UseCase,
    payerParticipantsState: AddItemPayerParticipantsState,
    onPayerSelected: (Long) -> Unit,
    onSelectionChanged: (Set<TripParticipant>) -> Unit,
    modifier: Modifier = Modifier
) {
    TsContentCard(
        modifier = modifier,
        isError = payerParticipantsState.isError
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TsParticipantsRadioGroup(
                label = R.string.expense_paid_by,
                modifier = Modifier.weight(1f),
                participants = payerParticipantsState.tripParticipants,
                selectedItemId = payerParticipantsState.expensePayerId,
                onItemSelected = onPayerSelected,
                itemLabel = { it.chipDisplayLabelName() }
            )

            when (useCase) {

                AddItemViewModel.UseCase.EXPENSE -> {
                    TsParticipantsCheckBoxGroup(
                        modifier = Modifier.weight(1f),
                        participants = payerParticipantsState.tripParticipants,
                        selectedParticipants = payerParticipantsState.selectedParticipants,
                        onSelectionChanged = onSelectionChanged
                    )
                }

                AddItemViewModel.UseCase.PAYMENT -> {
                    val payerId = payerParticipantsState.expensePayerId
                    val participants = payerParticipantsState.tripParticipants
                    val allowedPayees =
                        remember(payerId, participants) {
                            participants.filter { it.id != payerId }
                        }
                    TsParticipantsRadioGroup(
                        label = R.string.expense_paid_to,
                        modifier = Modifier.weight(1f),
                        participants = allowedPayees,
                        selectedItemId = payerParticipantsState.selectedParticipants.firstOrNull()?.id,
                        onItemSelected = { selectedItemId ->
                            val selectedParticipant = participants.find { it.id == selectedItemId }
                            val newSelection = selectedParticipant?.let { setOf(it) } ?: emptySet()
                            onSelectionChanged(newSelection)
                        },
                        itemLabel = { it.chipDisplayLabelName() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsPaidByPaidForCardPreviewExpense() {
    TsPaidByPaidForCard(
        useCase = AddItemViewModel.UseCase.EXPENSE,
        payerParticipantsState = getFakeAddExpensePayerParticipantsState(),
        onPayerSelected = {},
        onSelectionChanged = {},
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun TsPaidByPaidForCardPreviewPayment() {
    TsPaidByPaidForCard(
        useCase = AddItemViewModel.UseCase.PAYMENT,
        payerParticipantsState = getFakeAddExpensePayerParticipantsState(),
        onPayerSelected = {},
        onSelectionChanged = {},
        modifier = Modifier.fillMaxWidth()
    )
}