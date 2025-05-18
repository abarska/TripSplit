package com.anabars.tripsplit.navigation.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.anabars.tripsplit.R

@Composable
fun JoinExistingTripScreen(navController: NavController, modifier: Modifier = Modifier) {
    Text(text = stringResource(R.string.title_join_existing_trip))
}