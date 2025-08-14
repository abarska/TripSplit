package com.anabars.tripsplit.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.ui.model.ActionButton.ToolbarActionButton
import com.anabars.tripsplit.ui.model.TabItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    data class SharedUiState(
        val currentTripId: Long? = null,
        val selectedTabItem: TabItem = TabItem.Expenses,
        val toolbarActions: List<ToolbarActionButton> = emptyList(),
        val tabTitle: String? = null,
        val fabVisible: Boolean = false,
        val upButtonAction: (() -> Unit)? = null
    )

    sealed class SharedUiEvent {
        data class SetCurrentTrip(val tripId: Long?) : SharedUiEvent()
        data class SetTabItem(val tabItem: TabItem) : SharedUiEvent()
        data class SetToolbarActions(val actions: List<ToolbarActionButton>) : SharedUiEvent()
        data class SetTabTitle(val title: String?) : SharedUiEvent()
        data class SetFabVisibility(val visible: Boolean) : SharedUiEvent()
        data class UpdateUpButtonAction(val action: (() -> Unit)?) : SharedUiEvent()
    }

    private val _uiState = MutableStateFlow(SharedUiState())
    val uiState: StateFlow<SharedUiState> = _uiState.asStateFlow()

    fun onEvent(event: SharedUiEvent) {
        when (event) {
            is SharedUiEvent.SetCurrentTrip ->
                _uiState.update { it.copy(currentTripId = event.tripId) }

            is SharedUiEvent.SetTabItem ->
                _uiState.update { it.copy(selectedTabItem = event.tabItem) }

            is SharedUiEvent.SetToolbarActions ->
                _uiState.update { it.copy(toolbarActions = event.actions) }

            is SharedUiEvent.SetTabTitle ->
                _uiState.update { it.copy(tabTitle = event.title) }

            is SharedUiEvent.SetFabVisibility ->
                _uiState.update { it.copy(fabVisible = event.visible) }

            is SharedUiEvent.UpdateUpButtonAction -> {
                _uiState.update { it.copy(upButtonAction = event.action) }
            }
        }
    }

    sealed class SharedUiEffect {
        data class ShowSnackBar(@StringRes val resId: Int) : SharedUiEffect()
        data object FabClicked : SharedUiEffect()
    }

    private val _uiEffect = MutableSharedFlow<SharedUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onEffect(effect: SharedUiEffect) {
        when (effect) {
            is SharedUiEffect.FabClicked -> viewModelScope.launch {
                _uiEffect.emit(SharedUiEffect.FabClicked)
            }

            is SharedUiEffect.ShowSnackBar -> viewModelScope.launch {
                _uiEffect.emit(SharedUiEffect.ShowSnackBar(effect.resId))
            }
        }
    }
}