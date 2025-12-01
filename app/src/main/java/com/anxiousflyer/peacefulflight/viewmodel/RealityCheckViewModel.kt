package com.anxiousflyer.peacefulflight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anxiousflyer.peacefulflight.data.local.FlightSession
import com.anxiousflyer.peacefulflight.data.repository.FlightRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RealityCheckViewModel(
    private val flightRepository: FlightRepository
) : ViewModel() {

    val flightHistory: StateFlow<List<FlightSession>> = flightRepository.allFlights
        .map { flights ->
            // Only show completed flights (where we have both expected and actual fear)
            flights.filter { it.actualFear != null }
                .sortedBy { it.startTime }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getAverageDifference(flights: List<FlightSession>): Double {
        if (flights.isEmpty()) return 0.0
        val sumDiff = flights.sumOf { (it.expectedFear) - (it.actualFear ?: 0) }
        return sumDiff.toDouble() / flights.size
    }
}



