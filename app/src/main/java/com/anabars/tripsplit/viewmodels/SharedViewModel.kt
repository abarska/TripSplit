package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import com.anabars.tripsplit.ui.model.ToolbarAction
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

    private val _toolbarActions = MutableStateFlow<List<ToolbarAction>>(emptyList())
    val toolbarActions: StateFlow<List<ToolbarAction>> = _toolbarActions.asStateFlow()

    fun setToolbarActions(actions: List<ToolbarAction>) {
        _toolbarActions.value = actions
    }

    private val _tabTitle = MutableStateFlow<String?>(null)
    val tabTitle: StateFlow<String?> = _tabTitle.asStateFlow()
    fun setTabTitle(title: String?) {
        _tabTitle.value = title
    }
}