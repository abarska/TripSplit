package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.ViewModel
import com.anabars.tripsplit.ui.model.ActionButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    data class SharedUiState(
        val selectedTabIndex: Int = 1,
        val toolbarActions: List<ActionButton.ToolbarActionButton> = emptyList(),
        val tabTitle: String? = null,
    )

    sealed class SharedUiEvent {
        data class SetTabIndex(val index: Int) : SharedUiEvent()
        data class SetToolbarActions(val actions: List<ActionButton.ToolbarActionButton>) : SharedUiEvent()
        data class SetTabTitle(val title: String?) : SharedUiEvent()
    }

    private val _uiState = MutableStateFlow(SharedUiState())
    val uiState: StateFlow<SharedUiState> = _uiState.asStateFlow()

    fun onEvent(event: SharedUiEvent) {
        when (event) {
            is SharedUiEvent.SetTabIndex -> {
                _uiState.update { it.copy(selectedTabIndex = event.index) }
            }
            is SharedUiEvent.SetToolbarActions -> {
                _uiState.update { it.copy(toolbarActions = event.actions) }
            }
            is SharedUiEvent.SetTabTitle -> {
                _uiState.update { it.copy(tabTitle = event.title) }
            }
        }
    }

    private var _backHandler: (() -> Boolean)? = null

    fun setBackHandler(handler: (() -> Boolean)?) {
        _backHandler = handler
    }

    fun handleBack(): Boolean {
        return _backHandler?.invoke() ?: false
    }
}