package com.anabars.tripsplit.navigation

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.navigation.NavHostController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.model.ActionButton
import com.anabars.tripsplit.ui.model.TabItem

fun updateFabVisibility(
    currentRoute: String?,
    selectedTabItem: TabItem,
    updateFabVisibility: (Boolean) -> Unit
) {
    val fabVisible = when {
        currentRoute == AppScreens.TRIPS.route -> true
        currentRoute?.startsWith(AppScreens.TRIP_DETAILS.route) == true ->
            selectedTabItem in setOf(TabItem.Expenses, TabItem.Payments)

        else -> false
    }
    updateFabVisibility(fabVisible)
}

fun updateScreenTitle(
    currentRoute: String?,
    tripId: Long?,
    selectedTabItem: TabItem,
    context: Context,
    onTabTitleChange: (String) -> Unit
) {
    currentRoute?.let { route ->
        val screenTitle = when {
            route.startsWith(AppScreens.TRIPS.route) -> context.getString(R.string.title_trips)
            route.startsWith(AppScreens.ADD_EXPENSE.route) -> context.getString(R.string.title_new_expense)
            route.startsWith(AppScreens.ADD_PAYMENT.route) -> context.getString(R.string.title_new_payment)
            route.startsWith(AppScreens.SETTINGS.route) -> context.getString(R.string.title_settings)
            route.startsWith(AppScreens.ARCHIVE.route) -> context.getString(R.string.title_archive)
            route.startsWith(AppScreens.TRIP_OVERVIEW.route) -> context.getString(R.string.title_overview)

            route.startsWith(AppScreens.ADD_TRIP.route) -> {
                if (tripId == null || tripId == 0L) context.getString(R.string.title_new_trip)
                else context.getString(R.string.title_edit_trip)
            }

            route.startsWith(AppScreens.TRIP_DETAILS.route) -> {
                val prefixRes = R.string.title_trip_details
                val suffixRes = selectedTabItem.titleRes
                "${context.getString(prefixRes)}: ${context.getString(suffixRes)}"
            }

            else -> throw IllegalStateException("Unknown route: $route")
        }
        onTabTitleChange(screenTitle)
    }
}

fun updateToolbarActions(
    currentRoute: String?,
    navController: NavHostController,
    tripId: Long?,
    onTabActionsChange: (List<ActionButton.ToolbarActionButton>) -> Unit,
) {
    currentRoute?.let {
        val actions = when {
            currentRoute.startsWith(AppScreens.TRIP_DETAILS.route) ->
                getActionsForTripDetails(tripId, navController)

            currentRoute.startsWith(AppScreens.TRIP_OVERVIEW.route) ->
                getActionsForTripOverview(tripId, navController)

            else -> emptyList()
        }
        onTabActionsChange(actions)
    }
}

private fun getActionsForTripDetails(tripId: Long?, navController: NavHostController) =
    listOf(
        ActionButton.ToolbarActionButton(
            icon = Icons.Outlined.Info,
            contentDescriptionRes = R.string.title_overview,
            onClick = {
                tripId?.let { navController.navigate("${AppScreens.TRIP_OVERVIEW.route}/$it") }
            }
        ))

private fun getActionsForTripOverview(tripId: Long?, navController: NavHostController) =
    listOf(
        ActionButton.ToolbarActionButton(
            icon = Icons.Default.Edit,
            contentDescriptionRes = R.string.edit_item,
            onClick = {
                tripId?.let { navController.navigate("${AppScreens.ADD_TRIP.route}/$it") }
            })
    )