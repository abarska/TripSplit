package com.anabars.tripsplit.ui.screens.addtrip

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.ItemRowActionButton
import com.anabars.tripsplit.ui.components.SecondaryButton
import com.anabars.tripsplit.ui.itemrows.TripSplitItemRow
import com.anabars.tripsplit.ui.model.ActionButton

@Composable
fun CurrenciesSection(
    currencies: List<String>,
    onAddCurrencyButtonClick: () -> Unit,
    onDeleteCurrency: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InfoText(textRes = R.string.currencies_section_header)

        if (currencies.isNotEmpty()) {
            Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_small)))
            if (isPortrait) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(currencies) { code ->
                        ShowCurrency(code = code, onDeleteCurrency = onDeleteCurrency)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(currencies) { code ->
                        ShowCurrency(code = code, onDeleteCurrency = onDeleteCurrency)
                    }
                }
            }
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))

        SecondaryButton(textRes = R.string.add_a_currency) { onAddCurrencyButtonClick() }
    }
}

@Composable
private fun ShowCurrency(code: String, onDeleteCurrency: (String) -> Unit) {
    val button = ActionButton(
        icon = Icons.Default.Close,
        contentDescriptionRes = R.string.delete_participant,
    ) { onDeleteCurrency(code) }
    TripSplitItemRow {
        ItemRowActionButton(button)
        Spacer(Modifier.width(dimensionResource(R.dimen.vertical_spacer_small)))
        InfoText(text = code)
    }
}