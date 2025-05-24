package com.anabars.tripsplit.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R

@Composable
fun LabelText(modifier: Modifier = Modifier, @StringRes textRes: Int = 0, text: String = "") {
    val value = if (textRes != 0) stringResource(textRes) else text
    if (value.isNotEmpty()) {
        Text(
            modifier = modifier,
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun InfoText(modifier: Modifier = Modifier, @StringRes textRes: Int = 0, text: String = "") {
    val value = if (textRes != 0) stringResource(textRes) else text
    if (value.isNotEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                modifier = modifier,
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LabelTextPreview() {
    LabelText(text = stringResource(R.string.placeholder))
}

@Preview(showBackground = true)
@Composable
private fun InfoTextPreview() {
    InfoText(text = stringResource(R.string.placeholder))
}