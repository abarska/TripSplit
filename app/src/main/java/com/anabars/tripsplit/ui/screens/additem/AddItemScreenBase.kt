package com.anabars.tripsplit.ui.screens.additem

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.model.AmountCurrencyState
import com.anabars.tripsplit.ui.model.PayerParticipantsState
import com.anabars.tripsplit.ui.model.AddItemUiEffect
import com.anabars.tripsplit.viewmodels.AddItemViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddItemScreenBase(
    navController: NavHostController,
    onShowSnackbar: (Int) -> Unit,
    updateUpButtonAction: ((() -> Unit)?) -> Unit,
    portraitContent: @Composable (Modifier, AmountCurrencyState, PayerParticipantsState, AddItemViewModel) -> Unit,
    landscapeContent: @Composable (Modifier, AmountCurrencyState, PayerParticipantsState, AddItemViewModel) -> Unit
) {
    val viewModel: AddItemViewModel = hiltViewModel()
    val amountCurrencyState by viewModel.amountCurrencyState.collectAsState()
    val payerParticipantsState by viewModel.payerParticipantsState.collectAsState()

    LaunchedEffect(Unit) {
        updateUpButtonAction { viewModel.onIntent(AddItemIntent.OnBackPressed) }
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is AddItemUiEffect.NavigateBack -> {
                    if (effect.showWarning) onShowSnackbar(R.string.changes_discarded_warning)
                    navController.popBackStack()
                }

                is AddItemUiEffect.ShowSnackBar -> onShowSnackbar(effect.resId)
            }
        }
    }

    BackHandler { viewModel.onIntent(AddItemIntent.OnBackPressed) }

    DisposableEffect(Unit) {
        onDispose { updateUpButtonAction(null) }
    }

    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val scrollState = rememberScrollState()
    val modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(16.dp)

    if (isPortrait) {
        portraitContent(modifier, amountCurrencyState, payerParticipantsState, viewModel)
    } else {
        landscapeContent(modifier, amountCurrencyState, payerParticipantsState, viewModel)
    }
}
