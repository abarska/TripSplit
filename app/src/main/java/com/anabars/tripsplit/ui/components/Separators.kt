package com.anabars.tripsplit.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R

@Preview(showBackground = true)
@Composable
fun HorizontalSeparator() {
    Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))
    HorizontalDivider()
    Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))
}