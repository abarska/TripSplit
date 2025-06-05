package com.anabars.tripsplit.ui.utils

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R

@Composable
fun Modifier.inputWidthModifier(): Modifier {
    val windowSize = LocalWindowInfo.current.containerSize
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val screenWidthDp = with(LocalDensity.current) { windowSize.width.toDp() }

    return if (isPortrait && screenWidthDp < 600.dp) {
        fillMaxWidth()
    } else {
        width(dimensionResource(R.dimen.input_field_max_width))
    }
}