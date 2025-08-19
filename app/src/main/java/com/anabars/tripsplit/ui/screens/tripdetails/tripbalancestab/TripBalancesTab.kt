package com.anabars.tripsplit.ui.screens.tripdetails.tripbalancestab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import com.anabars.tripsplit.ui.widgets.TsBalanceItem
import com.anabars.tripsplit.viewmodels.BalanceViewModel

@Composable
fun TripBalancesTab(modifier: Modifier = Modifier) {

    val viewModel: BalanceViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .inputWidthModifier()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item { HorizontalDivider() }
        items(items = uiState) {
            TsBalanceItem(uiState = it)
            HorizontalDivider()
        }
    }
}