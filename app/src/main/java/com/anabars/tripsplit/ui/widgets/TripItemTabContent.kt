package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsPlusFab
import com.anabars.tripsplit.viewmodels.GroupedResult
import java.time.LocalDate

@Composable
fun <T> TripItemTabContent(
    result: GroupedResult<T>,
    placeholderTextRes: Int,
    fabClickRoute: String,
    successContent: @Composable (data: Map<LocalDate, List<T>>) -> Unit,
    navController: NavController,
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

        TsPlusFab(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            navController.navigate(fabClickRoute)
        }
    }
}