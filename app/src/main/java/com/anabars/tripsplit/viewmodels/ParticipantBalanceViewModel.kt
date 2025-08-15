package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.anabars.tripsplit.repository.ParticipantBalanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ParticipantBalanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val participantBalanceRepository: ParticipantBalanceRepository,
) : ViewModel() {}