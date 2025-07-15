package com.anabars.tripsplit.ui.listitems

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TsDateHeader(formattedDate: String, modifier: Modifier = Modifier) {
    OutlinedButton(
        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally),
        onClick = { }
    ) {
        Text(text = formattedDate)
    }
}