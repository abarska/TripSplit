package com.anabars.tripsplit.ui.widgets

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsInfoText

@Composable
fun TsPlaceholderView(
    @DrawableRes painterRes: Int,
    @StringRes contentDescriptionRes: Int,
    @StringRes textRes: Int = 0,
    text: String = "",
    imageSize: Dp = dimensionResource(R.dimen.placeholder_image_size),
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = painterRes),
            contentDescription = stringResource(contentDescriptionRes),
            modifier = Modifier.size(imageSize)
        )
        TsInfoText(text = text, textRes = textRes, isHeader = true)
    }
}