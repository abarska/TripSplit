package com.anabars.tripsplit.ui.listitems

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize

@Composable
fun TsItemRowHiddenButton(@StringRes labelRes: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick) {
            TsInfoText(
                textRes = labelRes,
                fontSize = TsFontSize.MEDIUM,
                textColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}