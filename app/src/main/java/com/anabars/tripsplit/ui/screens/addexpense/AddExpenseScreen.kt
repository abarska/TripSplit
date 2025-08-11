package com.anabars.tripsplit.ui.screens.addexpense

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.model.AddItemUiEffect
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.AmountChanged
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.CategoryChanged
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.CurrencySelected
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.DateSelected
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.OnBackPressed
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.ParticipantsSelected
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.PayerSelected
import com.anabars.tripsplit.ui.screens.addexpense.AddItemIntent.SaveItem
import com.anabars.tripsplit.ui.widgets.UseCase
import com.anabars.tripsplit.viewmodels.AddItemViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddExpenseScreen(
    navController: NavHostController,
    onTabTitleChange: (String) -> Unit,
    onShowSnackbar: (Int) -> Unit,
    setBackHandler: ((() -> Boolean)?) -> Unit
) {

    val viewModel: AddItemViewModel = hiltViewModel()
    val amountCurrencyState by viewModel.amountCurrencyState.collectAsState()
    val payerParticipantsState by viewModel.payerParticipantsState.collectAsState()
    val screenTitle = stringResource(R.string.title_new_expense)

    LaunchedEffect(Unit) {
        onTabTitleChange(screenTitle)
        setBackHandler {
            viewModel.onIntent(OnBackPressed)
            true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is AddItemUiEffect.NavigateBack -> navController.popBackStack()
                is AddItemUiEffect.ShowSnackBar -> onShowSnackbar(effect.resId)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { setBackHandler(null) }
    }

    BackHandler(enabled = true) {
        viewModel.onIntent(OnBackPressed)
    }

    val isPortrait =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val scrollState = rememberScrollState()
    val modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(16.dp)
    if (isPortrait) {
        AddExpensePortraitContent(
            amountCurrencyState = amountCurrencyState,
            payerParticipantsState = payerParticipantsState,
            onDateSelected = { viewModel.onIntent(DateSelected(it)) },
            onCategoryChange = { viewModel.onIntent(CategoryChanged(it)) },
            onExpenseAmountChanged = { viewModel.onIntent(AmountChanged(it)) },
            onCurrencySelected = { viewModel.onIntent(CurrencySelected(it)) },
            onPayerSelected = { viewModel.onIntent(PayerSelected(it)) },
            onParticipantsSelected = { viewModel.onIntent(ParticipantsSelected(it)) },
            onSaveExpense = { viewModel.onIntent(SaveItem(UseCase.EXPENSE)) },
            modifier = modifier
        )
    } else {
        AddExpenseLandscapeContent(
            amountCurrencyState = amountCurrencyState,
            payerParticipantsState = payerParticipantsState,
            onDateSelected = { viewModel.onIntent(DateSelected(it)) },
            onCategoryChange = { viewModel.onIntent(CategoryChanged(it)) },
            onExpenseAmountChanged = { viewModel.onIntent(AmountChanged(it)) },
            onCurrencySelected = { viewModel.onIntent(CurrencySelected(it)) },
            onPayerSelected = { viewModel.onIntent(PayerSelected(it)) },
            onParticipantsSelected = { viewModel.onIntent(ParticipantsSelected(it)) },
            onSaveExpense = { viewModel.onIntent(SaveItem(UseCase.EXPENSE)) },
            modifier = modifier
        )
    }
}