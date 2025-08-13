package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.anabars.tripsplit.R
import com.anabars.tripsplit.viewmodels.GroupedResult
import java.time.LocalDate

@Composable
fun <T> TripItemTabContent(
    result: GroupedResult<T>,
    placeholderTextRes: Int,
    successContent: @Composable (data: Map<LocalDate, List<T>>) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (result) {
            is GroupedResult.Loading ->
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            is GroupedResult.Empty ->
                TsPlaceholderView(
                    painterRes = R.drawable.empty_wallet_image,
                    contentDescriptionRes = R.string.empty_wallet_image,
                    textRes = placeholderTextRes
                )

            is GroupedResult.Success ->
                successContent(result.data)
        }
    }
}