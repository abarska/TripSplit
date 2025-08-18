package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun TsBalanceItem(modifier: Modifier = Modifier) {
    Row(modifier = modifier.inputWidthModifier(), verticalAlignment = Alignment.CenterVertically){
        TsInfoText()
    }
}