package com.anabars.tripsplit.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.viewmodels.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TsToolbar(
    isAtStartDestination: Boolean,
    onUpButtonClicked: () -> Unit,
    sharedState: SharedViewModel.SharedUiState,
    onDrawerToggle: () -> Unit
) {
    TopAppBar(
        title = {
            Crossfade(
                targetState = sharedState.tabTitle ?: stringResource(R.string.app_name),
                animationSpec = tween(durationMillis = 200)
            ) { title ->
                TsInfoText(text = title, fontSize = TsFontSize.MEDIUM)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        ),
        navigationIcon = {
            TsNavigationIcon(
                isStartDestination = isAtStartDestination,
                onMenuClick = onDrawerToggle,
                onBackClick = { sharedState.upButtonAction?.invoke() ?: onUpButtonClicked() }
            )
        },
        actions = {
            sharedState.toolbarActions.forEach { action ->
                IconButton(onClick = action.onClick) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = stringResource(action.contentDescriptionRes)
                    )
                }
            }
        },
    )
}

@Preview
@Composable
private fun TsToolbarPreviewStartDestination() {
    TsToolbar(
        sharedState = SharedViewModel.SharedUiState(),
        onDrawerToggle = {},
        isAtStartDestination = true,
        onUpButtonClicked = {}
    )
}

@Preview
@Composable
private fun TsToolbarPreviewOtherDestination() {
    TsToolbar(
        sharedState = SharedViewModel.SharedUiState(),
        onDrawerToggle = {},
        isAtStartDestination = false,
        onUpButtonClicked = {}
    )
}
