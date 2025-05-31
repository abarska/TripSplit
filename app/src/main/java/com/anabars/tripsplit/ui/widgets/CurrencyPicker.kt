package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R

@Composable
fun CurrencyPicker(
    key: String,
    currencies: List<String>,
    selectedCurrency: String,
    onCurrencySelected: (String, String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredCurrencies = currencies.filter {
        it.contains(searchQuery, ignoreCase = true)
    }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = selectedCurrency)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = dimensionResource(R.dimen.dropdown_menu_height))
                .width(dimensionResource(R.dimen.dropdown_menu_width))
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(text = stringResource(R.string.search_currency)) },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_currency)
                    )
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))

            if (currencies.isEmpty()) {
                CircularProgressIndicator(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            } else {
                filteredCurrencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(text = currency) },
                        onClick = {
                            onCurrencySelected(key, currency)
                            expanded = false
                            searchQuery = ""
                        }
                    )
                }
            }
        }
    }
}