package com.anabars.tripsplit.ui.screens.tripdetails.tripbalancestab

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anabars.tripsplit.ui.components.LayoutType
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.TsRadioGroup
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import com.anabars.tripsplit.ui.widgets.TsBalanceItem
import com.anabars.tripsplit.viewmodels.BalanceViewModel

@Composable
fun TripBalancesTab(modifier: Modifier = Modifier) {

    val viewModel: BalanceViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        items(items = uiState.balances) {
            TsBalanceItem(modifier = Modifier.inputWidthModifier().padding(vertical = 8.dp), balance = it)
            HorizontalDivider(modifier = Modifier.inputWidthModifier())
        }

        item {
            TsRadioGroup(
                modifier = Modifier.inputWidthModifier().padding(vertical = 16.dp),
                items = uiState.currencies,
                onItemSelected = {},
                layout = LayoutType.Flow
            ) {
                TsInfoText(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    text = it.code,
                    fontSize = TsFontSize.MEDIUM
                )
            }
        }
    }
}