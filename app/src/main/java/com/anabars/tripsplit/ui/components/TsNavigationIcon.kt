package com.anabars.tripsplit.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R

@Composable
fun TsNavigationIcon(
    isStartDestination: Boolean,
    onMenuClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val icon = if (isStartDestination) Icons.Filled.Menu else Icons.AutoMirrored.Filled.ArrowBack
    val description = if (isStartDestination) R.string.menu else R.string.back
    val onClick = if (isStartDestination) onMenuClick else onBackClick

    IconButton(onClick = onClick) {
        Icon(imageVector = icon, contentDescription = stringResource(description))
    }
}

@Preview
@Composable
private fun TsNavigationIconHamburgerPreview() {
    TsNavigationIcon(true, {}, {})
}


@Preview
@Composable
private fun TsNavigationIconArrowPreview() {
    TsNavigationIcon(false, {}, {})
}