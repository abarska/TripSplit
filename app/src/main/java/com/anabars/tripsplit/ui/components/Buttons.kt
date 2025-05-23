package com.anabars.tripsplit.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R

@Composable
fun TripSplitFab(
    modifier: Modifier = Modifier,
    iconVector: ImageVector? = null,
    @DrawableRes iconRes: Int = 0,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    @StringRes contentDescription: Int = R.string.action_button,
    isEnabled: Boolean = true,
    onClick: () -> Unit
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
        val icon = if (iconRes != 0) ImageVector.vectorResource(id = iconRes) else iconVector
        icon?.let {
            Image(
                modifier = Modifier.fillMaxSize(),
                imageVector = it,
                contentDescription = stringResource(contentDescription)
            )
        }
    }
}

@Composable
fun MainButton(
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
        modifier = modifier
            .width(dimensionResource(R.dimen.button_width))
            .padding(16.dp)
    ) {
        val value = if (textRes != 0) stringResource(textRes) else text
        if (value.isNotEmpty()) {
            ButtonText(text = value)
        }
    }
}