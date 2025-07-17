package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _tabTitle = MutableStateFlow<String?>(null)
    val tabTitle: StateFlow<String?> = _tabTitle.asStateFlow()

    fun setTabTitle(title: String?) {
        _tabTitle.value = title
    }
}