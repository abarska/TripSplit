package com.anabars.tripsplit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.model.Trip
import com.anabars.tripsplit.ui.components.MainButton
import com.anabars.tripsplit.ui.components.ShortInputTextField
import com.anabars.tripsplit.viewmodels.TripViewModel

@Composable
fun NewTripScreen(
    navController: NavController,
    tripViewModel: TripViewModel,
    modifier: Modifier = Modifier,
) {

    var tripNameState by remember { mutableStateOf("") }
    var tripDescriptionState by remember { mutableStateOf("") }
    var tripNameErrorMessage by remember { mutableIntStateOf(0) }
    var tripNameError by remember { mutableStateOf(false) }

    val onTripNameChanged = { input: String ->
        tripNameState = input
        tripNameErrorMessage = 0
        tripNameError = false
    }
    val onTripDescriptionChanged = { input: String ->
        tripDescriptionState = input
    }
    val onSaveButtonClicked = {
        if (tripViewModel.fieldNotEmpty(value = tripNameState)) {
            tripViewModel.addTrip(
                Trip(title = tripNameState, description = tripDescriptionState)
            )
            tripNameState = ""
            tripDescriptionState = ""
        } else {
            tripNameError = true
            tripNameErrorMessage = R.string.error_mandatory_field
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShortInputTextField(
            modifier = Modifier.fillMaxWidth(),
            value = tripNameState,
            isError = tripNameError,
            labelRes = if (tripNameErrorMessage > 0 && tripNameError) tripNameErrorMessage else R.string.new_trip_title_hint,
            onValueChanged = onTripNameChanged
        )

        Spacer(Modifier.height(16.dp))

        ShortInputTextField(
            modifier = Modifier.fillMaxWidth(),
            value = tripDescriptionState,
            labelRes = R.string.new_trip_description_hint,
            onValueChanged = onTripDescriptionChanged
        )

        Spacer(Modifier.height(16.dp))

        MainButton(textRes = R.string.save, onClick = onSaveButtonClicked)
    }
}