package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private var _backHandler: (() -> Boolean)? = null

    fun setBackHandler(handler: (() -> Boolean)?) {
        _backHandler = handler
    }

    fun handleBack(): Boolean {
        return _backHandler?.invoke() ?: false
    }
}