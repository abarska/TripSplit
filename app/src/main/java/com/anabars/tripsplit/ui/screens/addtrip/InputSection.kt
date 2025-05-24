package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.ShortInputTextField

@Composable
fun InputSection(
    tripName: String,
    tripNameError: Boolean,
    tripNameErrorMessage: Int,
    onTripNameChanged: (String) -> Unit,
    tripDescription: String,
    onTripDescriptionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ShortInputTextField(
            value = tripName,
            isError = tripNameError,
            labelRes = if (tripNameErrorMessage > 0 && tripNameError) tripNameErrorMessage else R.string.new_trip_title_hint,
            onValueChanged = onTripNameChanged,
            modifier = modifier
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))

        ShortInputTextField(
            value = tripDescription,
            labelRes = R.string.new_trip_description_hint,
            onValueChanged = onTripDescriptionChanged,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InputSectionPreview() {
    InputSection(
        tripName = "name",
        tripNameError = false,
        tripNameErrorMessage = 0,
        onTripNameChanged = {},
        tripDescription = "description",
        onTripDescriptionChanged = {}
    )
}