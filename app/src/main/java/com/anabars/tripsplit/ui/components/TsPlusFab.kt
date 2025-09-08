package com.anabars.tripsplit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R

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