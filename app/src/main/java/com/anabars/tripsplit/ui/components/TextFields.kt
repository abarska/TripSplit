package com.anabars.tripsplit.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.ui.utils.TsFontSize

@Composable
fun TsLabelText(
    modifier: Modifier = Modifier,
    @StringRes textRes: Int = 0,
    text: String = "",
    isError: Boolean = false
) {
    val value = if (textRes != 0) stringResource(textRes) else text
    if (value.isNotEmpty()) {
        Text(
            modifier = modifier,
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                color =
                    if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        )
    }
}

@Composable
fun TsInfoText(
    modifier: Modifier = Modifier,
    @StringRes textRes: Int = 0,
    text: String = "",
    fontSize: TsFontSize = TsFontSize.SMALL,
    maxLines: Int = 10,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val value = if (textRes != 0) stringResource(textRes) else text
    if (value.isNotEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                modifier = modifier,
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = fontSize.sp,
                    color = textColor
                ),
                maxLines = maxLines
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsLabelTextPreview() {
    TsLabelText(text = "Placeholder")
}

@Preview(showBackground = true)
@Composable
private fun TsErrorLabelTextPreview() {
    TsLabelText(text = "Placeholder", isError = true)
}

@Preview(showBackground = true)
@Composable
private fun TsInfoTextSmallPreview() {
    TsInfoText(text = "Placeholder")
}

@Preview(showBackground = true)
@Composable
private fun TsInfoTextMediumPreview() {
    TsInfoText(text = "Placeholder", fontSize = TsFontSize.MEDIUM)
}

@Preview(showBackground = true)
@Composable
private fun TsInfoTextLargePreview() {
    TsInfoText(text = "Placeholder", fontSize = TsFontSize.LARGE)
}