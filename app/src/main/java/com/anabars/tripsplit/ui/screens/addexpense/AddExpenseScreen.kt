package com.anabars.tripsplit.ui.screens.addexpense

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.model.AddExpenseUiEffect
import com.anabars.tripsplit.ui.screens.addexpense.AddExpenseIntent.*
import com.anabars.tripsplit.viewmodels.AddExpenseViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddExpenseScreen(
    navController: NavHostController,
    onTabTitleChange: (String) -> Unit,
    setBackHandler: ((() -> Boolean)?) -> Unit
) {

    val context = LocalContext.current
    val viewModel: AddExpenseViewModel = hiltViewModel()

    val dateCategoryState by viewModel.dateCategoryState.collectAsState()
    val amountCurrencyState by viewModel.amountCurrencyState.collectAsState()
    val payerParticipantsState by viewModel.payerParticipantsState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

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
                is AddExpenseUiEffect.NavigateBack -> {
                    navController.popBackStack()
                }
                is AddExpenseUiEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(context.getString(effect.resId))
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { setBackHandler(null) }
    }

    BackHandler(enabled = true) {
        viewModel.onIntent(OnBackPressed)
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            }
        }
    ) { paddingValues ->
        val isPortrait =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
        val scrollState = rememberScrollState()
        val modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
        if (isPortrait) {
            AddExpensePortraitContent(
                dateCategoryState = dateCategoryState,
                amountCurrencyState = amountCurrencyState,
                payerParticipantsState = payerParticipantsState,
                onDateSelected = { viewModel.onIntent(DateSelected(it)) },
                onCategoryChange = { viewModel.onIntent(CategoryChanged(it)) },
                onExpenseAmountChanged = { viewModel.onIntent(AmountChanged(it)) },
                onCurrencySelected = { viewModel.onIntent(CurrencySelected(it)) },
                onPayerSelected = { viewModel.onIntent(PayerSelected(it)) },
                onParticipantsSelected = { viewModel.onIntent(ParticipantsSelected(it)) },
                onSaveExpense = { viewModel.onIntent(SaveExpense) },
                modifier = modifier
            )
        } else {
            AddExpenseLandscapeContent(
                dateCategoryState = dateCategoryState,
                amountCurrencyState = amountCurrencyState,
                payerParticipantsState = payerParticipantsState,
                onDateSelected = { viewModel.onIntent(DateSelected(it)) },
                onCategoryChange = { viewModel.onIntent(CategoryChanged(it)) },
                onExpenseAmountChanged = { viewModel.onIntent(AmountChanged(it)) },
                onCurrencySelected = { viewModel.onIntent(CurrencySelected(it)) },
                onPayerSelected = { viewModel.onIntent(PayerSelected(it)) },
                onParticipantsSelected = { viewModel.onIntent(ParticipantsSelected(it)) },
                onSaveExpense = { viewModel.onIntent(SaveExpense) },
                modifier = modifier
            )
        }
    }
}