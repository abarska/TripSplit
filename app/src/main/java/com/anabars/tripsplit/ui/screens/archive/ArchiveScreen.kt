package com.anabars.tripsplit.ui.screens.archive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.navigation.Routes
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.listitems.TsItemRow
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import com.anabars.tripsplit.viewmodels.ArchiveViewModel

@Composable
fun ArchiveScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onTabTitleChange: (String) -> Unit
) {

    val archiveViewModel: ArchiveViewModel = hiltViewModel()
    val archivedTrips by archiveViewModel.archivedTrips.collectAsState()

    val screenTitle = stringResource(R.string.title_archive)
    LaunchedEffect(Unit) {
        onTabTitleChange(screenTitle)
    }

    LazyColumn(
        modifier = Modifier.inputWidthModifier().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(
            items = archivedTrips,
            key = { trip -> trip.id }
        ) { trip ->
            TsItemRow(
                modifier = modifier.inputWidthModifier(),
                onItemClick = {
                    navController.navigate(Routes.ROUTE_TRIP_DETAILS + "/${trip.id}")
                }
            ) {
                TsInfoText(
                    modifier = Modifier.padding(16.dp),
                    text = trip.title,
                    fontSize = TsFontSize.MEDIUM
                )
            }
        }
    }
}