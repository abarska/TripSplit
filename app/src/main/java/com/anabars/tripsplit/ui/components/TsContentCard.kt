package com.anabars.tripsplit.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeTripCurrencies
import com.anabars.tripsplit.ui.utils.getFakeTripParticipants
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun TsContentCard(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    isEnabled: Boolean = true,
    isHighlighted: Boolean = false,
    onItemClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val backgroundColor =
        if (isHighlighted) MaterialTheme.colorScheme.primaryContainer
        else Color.Transparent
    val strokeColor =
        if (isError) SolidColor(MaterialTheme.colorScheme.error)
        else SolidColor(MaterialTheme.colorScheme.outline)
    Card(
        modifier = modifier,
        enabled = isEnabled,
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius)),
        colors = CardDefaults.cardColors().copy(containerColor = backgroundColor),
        border = CardDefaults.outlinedCardBorder().copy(
            width = if (isError) 2.dp else 1.dp,
            brush = strokeColor
        ),
        onClick = onItemClick,
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun TsContentCardPreviewWithoutError() {
    Column(
        modifier = Modifier
            .inputWidthModifier()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TsInfoText(
            text = "Antarctica",
            fontSize = TsFontSize.LARGE
        )
        TsInfoText(
            text = getFakeTripParticipants().joinToString { it.name },
            fontSize = TsFontSize.MEDIUM
        )
        TsInfoText(
            text = getFakeTripCurrencies().joinToString { it.code },
            fontSize = TsFontSize.MEDIUM
        )
    }
}