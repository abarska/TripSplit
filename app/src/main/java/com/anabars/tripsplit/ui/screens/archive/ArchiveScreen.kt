package com.anabars.tripsplit.ui.screens.archive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anabars.tripsplit.R

@Composable
fun ArchiveScreen(
    modifier: Modifier = Modifier,
    onTabTitleChange: (String) -> Unit
) {
    val screenTitle = stringResource(R.string.title_archive)
    LaunchedEffect(Unit) {
        onTabTitleChange(screenTitle)
    }
}