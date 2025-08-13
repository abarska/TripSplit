package com.anabars.tripsplit.ui.screens.trips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripStatus
import com.anabars.tripsplit.navigation.Routes
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.TsItemRowActionButton
import com.anabars.tripsplit.ui.components.TsOutlinedButton
import com.anabars.tripsplit.ui.listitems.TsItemRow
import com.anabars.tripsplit.ui.model.ActionButton
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import com.anabars.tripsplit.viewmodels.TripsViewModel

@Composable
fun TripsScreen(
    navController: NavController,
    onTabTitleChange: (String) -> Unit,
    setToolbarActions: (List<ActionButton.ToolbarActionButton>) -> Unit,
    modifier: Modifier = Modifier
) {

    val tripsViewModel: TripsViewModel = hiltViewModel()
    val tripsGrouped by tripsViewModel.tripsGroupedByStatus.collectAsState()
    val ascendingOrder by tripsViewModel.ascendingOrder.collectAsState()
    val screenTitle = stringResource(R.string.title_trips)

    LaunchedEffect(screenTitle) {
        onTabTitleChange(screenTitle)
    }

    LaunchedEffect(ascendingOrder) {
        setToolbarActions(
            listOf(
                ActionButton.ToolbarActionButton(
                    icon = if (ascendingOrder) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                    contentDescriptionRes = if (ascendingOrder) R.string.arrow_down else R.string.arrow_up,
                    onClick = { tripsViewModel.toggleSorting() }
                )
            )
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            setToolbarActions(emptyList())
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.full_screen_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        tripsGrouped.forEach { (status, trips) ->
            item(key = "status_${status.name}") {
                TsOutlinedButton(
                    text = stringResource(id = status.labelRes),
                    modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally)
                ) {}
            }
            items(
                items = trips,
                key = { trip -> trip.id }
            ) { trip ->
                TsItemRow(
                    modifier = modifier.inputWidthModifier(),
                    onItemClick = {
                        navController.navigate(Routes.ROUTE_TRIP_DETAILS + "/${trip.id}")
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TsInfoText(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f),
                            text = trip.title,
                            fontSize = TsFontSize.MEDIUM
                        )
                        val button = ActionButton.ChipActionButton(
                            icon = Icons.Outlined.Archive,
                            iconSize = 32.dp,
                            contentDescriptionRes = R.string.archive_item,
                        ) {
                            tripsViewModel.updateTripStatus(
                                id = trip.id,
                                status = TripStatus.ARCHIVED
                            )
                        }
                        TsItemRowActionButton(button)
                    }
                }
            }
        }
    }
}
