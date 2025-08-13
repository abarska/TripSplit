package com.anabars.tripsplit.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.model.ActionButton
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Preview(showBackground = true)
@Composable
fun TsPlusFab(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    isEnabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    FloatingActionButton(
        onClick = { if (isEnabled) onClick() },
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 4.dp,
            pressedElevation = 0.dp,
            focusedElevation = 4.dp,
            hoveredElevation = 4.dp
        )
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            imageVector = Icons.Outlined.Add,
            contentDescription = stringResource(R.string.plus_button)
        )
    }
}

@Composable
fun TsMainButton(
    modifier: Modifier = Modifier,
    @StringRes textRes: Int = 0,
    text: String = "",
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(dimensionResource(R.dimen.button_corner_radius)),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 0.dp,
            focusedElevation = 4.dp,
            hoveredElevation = 4.dp,
            disabledElevation = 0.dp
        ),
        modifier = modifier.then(Modifier.inputWidthModifier()),
    ) {
        TsInfoText(
            textRes = textRes,
            text = text,
            fontSize = TsFontSize.MEDIUM,
            textColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TsMainButtonPreview() {
    TsMainButton(text = "Placeholder") { }
}

@Composable
fun TsItemRowActionButton(actionButton: ActionButton.ChipActionButton) {
    Box(
        modifier = Modifier
            .size(actionButton.iconSize)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = actionButton.enabled,
                onClick = actionButton.onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = actionButton.icon,
            contentDescription = stringResource(actionButton.contentDescriptionRes),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TsItemRowActionButtonPreview() {
    TsItemRowActionButton(
        ActionButton.ChipActionButton(icon = Icons.Default.Pause) {}
    )
}

@Composable
fun TsOutlinedButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        enabled = enabled,
        border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary),
        onClick = onClick
    ) {
        TsInfoText(
            text = text,
            fontSize = TsFontSize.MEDIUM,
            textColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TsOutlinedButtonPreview() {
    TsOutlinedButton(
        text = "Placeholder",
        modifier = Modifier.inputWidthModifier(),
        onClick = {}
    )
}