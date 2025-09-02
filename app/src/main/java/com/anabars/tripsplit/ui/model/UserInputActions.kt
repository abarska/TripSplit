package com.anabars.tripsplit.ui.model

data class UserInputActions(
    val onInputChange: (String) -> Unit = {},
    val onMultiplicatorChange: (Int) -> Unit = {},
    val onConfirm: () -> Unit = {},
    val onDismiss: () -> Unit = {}
)
