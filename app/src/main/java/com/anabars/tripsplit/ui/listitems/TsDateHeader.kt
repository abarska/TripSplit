package com.anabars.tripsplit.ui.listitems

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize

@Composable
fun TsDateHeader(formattedDate: String, modifier: Modifier = Modifier) {
    OutlinedButton(
        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally),
        onClick = { },
        border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary)
    ) {
        TsInfoText(
            text = formattedDate,
            fontSize = TsFontSize.MEDIUM,
            textColor = MaterialTheme.colorScheme.primary
        )
    }
}