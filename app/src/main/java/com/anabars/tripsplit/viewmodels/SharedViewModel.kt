package com.anabars.tripsplit.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anabars.tripsplit.ui.model.ActionButton.*
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
        val selectedTabIndex: Int = 1,
        val toolbarActions: List<ToolbarActionButton> = emptyList(),
        val tabTitle: String? = null,
        val fabVisible: Boolean = false
    )

    sealed class SharedUiEvent {
        data class SetCurrentTrip(val tripId: Long?) : SharedUiEvent()
        data class SetTabIndex(val index: Int) : SharedUiEvent()
        data class SetToolbarActions(val actions: List<ToolbarActionButton>) : SharedUiEvent()
        data class SetTabTitle(val title: String?) : SharedUiEvent()
        data class SetFabVisibility(val visible: Boolean) : SharedUiEvent()
    }

    private val _uiState = MutableStateFlow(SharedUiState())
    val uiState: StateFlow<SharedUiState> = _uiState.asStateFlow()

    fun onEvent(event: SharedUiEvent) {
        when (event) {
            is SharedUiEvent.SetCurrentTrip ->
                _uiState.update { it.copy(currentTripId = event.tripId) }

            is SharedUiEvent.SetTabIndex ->
                _uiState.update { it.copy(selectedTabIndex = event.index) }

            is SharedUiEvent.SetToolbarActions ->
                _uiState.update { it.copy(toolbarActions = event.actions) }

            is SharedUiEvent.SetTabTitle ->
                _uiState.update { it.copy(tabTitle = event.title) }

            is SharedUiEvent.SetFabVisibility ->
                _uiState.update { it.copy(fabVisible = event.visible) }
        }
    }

    sealed class SharedUiEffect {
        data class ShowSnackBar(@StringRes val resId: Int) : SharedUiEffect()
        data class NavigateTo(val route: String) : SharedUiEffect()
    }

    private val _uiEffect = MutableSharedFlow<SharedUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onEffect(effect: SharedUiEffect) {
        when (effect) {
            is SharedUiEffect.NavigateTo -> viewModelScope.launch {
                _uiEffect.emit(SharedUiEffect.NavigateTo(effect.route))
            }

            is SharedUiEffect.ShowSnackBar -> viewModelScope.launch {
                _uiEffect.emit(SharedUiEffect.ShowSnackBar(effect.resId))
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