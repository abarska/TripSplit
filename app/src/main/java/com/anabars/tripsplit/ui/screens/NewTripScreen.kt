package com.anabars.tripsplit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.model.Trip
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.MainButton
import com.anabars.tripsplit.ui.components.SecondaryButton
import com.anabars.tripsplit.ui.components.ShortInputTextField
import com.anabars.tripsplit.ui.dialogs.AddParticipantDialog
import com.anabars.tripsplit.viewmodels.TripViewModel

@Composable
fun NewTripScreen(
    navController: NavController,
    tripViewModel: TripViewModel,
    modifier: Modifier = Modifier,
) {

    var tripName by rememberSaveable { mutableStateOf("") }
    var tripNameErrorMessage by rememberSaveable { mutableIntStateOf(0) }
    var tripNameError by rememberSaveable { mutableStateOf(false) }

    var tripDescription by rememberSaveable { mutableStateOf("") }

    var newParticipantName by rememberSaveable { mutableStateOf("") }

    var showDialog by rememberSaveable { mutableStateOf(false) }

    val onTripNameChanged = { input: String ->
        tripName = input
        tripNameErrorMessage = 0
        tripNameError = false
    }

    val onTripDescriptionChanged = { input: String ->
        tripDescription = input
    }

    val onSaveTrip = {
        if (tripViewModel.fieldNotEmpty(value = tripName)) {
            tripViewModel.saveTrip(
                Trip(title = tripName, description = tripDescription)
            )
            tripName = ""
            tripDescription = ""
        } else {
            tripNameError = true
            tripNameErrorMessage = R.string.error_mandatory_field
        }
    }

    val onAddParticipant = {
        if (tripViewModel.fieldNotEmpty(value = newParticipantName)) {
            tripViewModel.addParticipant(newParticipantName)
        }
        showDialog = false
        newParticipantName = ""
    }

    val onDismissAddParticipantDialog = {
        showDialog = false
        newParticipantName = ""
    }

    val participants by tripViewModel.participants.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showDialog) {
            AddParticipantDialog(
                name = newParticipantName,
                onNameChange = { newName -> newParticipantName = newName },
                onSave = { onAddParticipant() },
                onDismiss = { onDismissAddParticipantDialog() },
            )
        } else {
            ShortInputTextField(
                modifier = Modifier.fillMaxWidth(),
                value = tripName,
                isError = tripNameError,
                labelRes = if (tripNameErrorMessage > 0 && tripNameError) tripNameErrorMessage else R.string.new_trip_title_hint,
                onValueChanged = onTripNameChanged
            )

            Spacer(Modifier.height(16.dp))

            ShortInputTextField(
                modifier = Modifier.fillMaxWidth(),
                value = tripDescription,
                labelRes = R.string.new_trip_description_hint,
                onValueChanged = onTripDescriptionChanged
            )

            Spacer(Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            InfoText(textRes = R.string.participants_section_header)

            if (participants.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                participants.forEach { name ->
                    InfoText(text = name)
                }
            }

            Spacer(Modifier.height(16.dp))

            SecondaryButton(textRes = R.string.add_a_participant) { showDialog = true }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            MainButton(textRes = R.string.save) { onSaveTrip() }
        }
    }
}