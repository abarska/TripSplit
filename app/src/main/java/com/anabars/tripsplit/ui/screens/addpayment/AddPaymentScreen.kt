package com.anabars.tripsplit.ui.screens.addpayment

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.anabars.tripsplit.R

@Composable
fun AddPaymentScreen(
    navController: NavHostController,
    onTabTitleChange: (String) -> Unit,
    setBackHandler: ((() -> Boolean)?) -> Unit
) {

    val screenTitle = stringResource(R.string.title_new_payment)
    LaunchedEffect(Unit) {
        onTabTitleChange(screenTitle)
    }

    Text(text = "under construction", modifier = Modifier.padding(16.dp))
}