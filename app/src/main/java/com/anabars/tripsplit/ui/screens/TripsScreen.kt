package com.anabars.tripsplit.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TripSplitFab

@Composable
fun TripsScreen(navController: NavController, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        TripSplitFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
            iconVector = Icons.Outlined.Add,
            contentDescription = R.string.add_a_new_trip,
        ) {
            Log.d("marusya", "TripsScreen: click FAB")
        }
    }
}