package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.anabars.tripsplit.repository.TripPaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TripPaymentViewModel @Inject constructor(
    val tripPaymentRepository: TripPaymentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tripId: Long = savedStateHandle.get<Long>("id")
        ?: throw IllegalStateException("Trip ID is required for TripPaymentViewModel")
}