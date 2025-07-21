package com.anabars.tripsplit.ui.screens.trips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.outlined.Add
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
import com.anabars.tripsplit.ui.components.TsFab
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.TsOutlinedButton
import com.anabars.tripsplit.ui.listitems.TsItemRow
import com.anabars.tripsplit.ui.model.ActionButton
import com.anabars.tripsplit.ui.screens.AppScreens
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

    val screenTitle = stringResource(R.string.title_trips)

    LaunchedEffect(Unit) {
        onTabTitleChange(screenTitle)
        setToolbarActions(
            listOf(
                ActionButton.ToolbarActionButton(
                    icon = Icons.Filled.ArrowDownward,
                    contentDescriptionRes = R.string.reverse_sorting,
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

    Box(
        modifier = modifier.padding(dimensionResource(R.dimen.full_screen_padding))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            tripsGrouped.forEach { (status, trips) ->
                item {
                    TsOutlinedButton(
                        text = stringResource(id = status.labelRes),
                        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally)
                    ) {}
                }

                items(trips, key = { it.id }) { trip ->
                    TsItemRow(
                        modifier = modifier.inputWidthModifier(),
                        onItemClick = {
                            navController.navigate(AppScreens.ROUTE_TRIP_DETAILS + "/${trip.id}")
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
        TsFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            iconVector = Icons.Outlined.Add,
            contentDescription = R.string.add_a_new_trip,
        ) {
            navController.navigate(AppScreens.ROUTE_ADD_TRIP) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}
