package com.anabars.tripsplit.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.anabars.tripsplit.R

@Composable
fun NewTripScreen(navController: NavController, modifier: Modifier = Modifier) {
    Text(text = stringResource(R.string.title_new_trip))
}