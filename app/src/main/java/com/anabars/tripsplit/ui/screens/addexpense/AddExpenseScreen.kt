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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
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
import com.anabars.tripsplit.ui.model.AddExpenseEvent
import com.anabars.tripsplit.viewmodels.AddExpenseViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel

@Composable
fun AddExpenseScreen(
    navController: NavHostController,
    onTabTitleChange: (String) -> Unit,
    sharedViewModel: SharedViewModel
) {

    val context = LocalContext.current
    val viewModel: AddExpenseViewModel = hiltViewModel()

    val dateCategoryState by viewModel.dateCategoryState.collectAsState()
    val amountCurrencyState by viewModel.amountCurrencyState.collectAsState()
    val payerParticipantsState by viewModel.payerParticipantsState.collectAsState()
    val addExpenseErrorRes by viewModel.addExpenseErrorRes.collectAsState()
    val navigateBackState by viewModel.navigateBackState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    val screenTitle = stringResource(R.string.title_new_expense)
    LaunchedEffect(Unit) {
        onTabTitleChange(screenTitle)
    }

    BackHandler {
        if (!sharedViewModel.handleBack()) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(navigateBackState, addExpenseErrorRes) {
        if (navigateBackState) {
            navController.popBackStack()
            viewModel.onNavigatedBack()
        }

        if (addExpenseErrorRes != 0) {
            snackBarHostState.showSnackbar(
                message = context.getString(addExpenseErrorRes),
                duration = SnackbarDuration.Short
            )
        }
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
                addExpenseErrorRes = addExpenseErrorRes,
                dateCategoryState = dateCategoryState,
                amountCurrencyState = amountCurrencyState,
                payerParticipantsState = payerParticipantsState,
                onDateSelected = { viewModel.onEvent(AddExpenseEvent.DateSelected(it)) },
                onCategoryChange = { viewModel.onEvent(AddExpenseEvent.CategoryChanged(it)) },
                onExpenseAmountChanged = { viewModel.onEvent(AddExpenseEvent.AmountChanged(it)) },
                onCurrencySelected = { viewModel.onEvent(AddExpenseEvent.CurrencySelected(it)) },
                onPayerSelected = { viewModel.onEvent(AddExpenseEvent.PayerSelected(it)) },
                onParticipantsSelected = { viewModel.onEvent(AddExpenseEvent.ParticipantsSelected(it)) },
                onSaveExpense = { viewModel.saveExpense() },
                modifier = modifier
            )
        } else {
            AddExpenseLandscapeContent(
                addExpenseErrorRes = addExpenseErrorRes,
                dateCategoryState = dateCategoryState,
                amountCurrencyState = amountCurrencyState,
                payerParticipantsState = payerParticipantsState,
                onDateSelected = { viewModel.onEvent(AddExpenseEvent.DateSelected(it)) },
                onCategoryChange = { viewModel.onEvent(AddExpenseEvent.CategoryChanged(it)) },
                onExpenseAmountChanged = { viewModel.onEvent(AddExpenseEvent.AmountChanged(it)) },
                onCurrencySelected = { viewModel.onEvent(AddExpenseEvent.CurrencySelected(it)) },
                onPayerSelected = { viewModel.onEvent(AddExpenseEvent.PayerSelected(it)) },
                onParticipantsSelected = { viewModel.onEvent(AddExpenseEvent.ParticipantsSelected(it)) },
                onSaveExpense = { viewModel.saveExpense() },
                modifier = modifier
            )
        }
    }
}