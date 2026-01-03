package com.trevorwiebe.apogee.schedule.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.trevorwiebe.apogee.global.domain.FifteenMinSlot
import com.trevorwiebe.apogee.global.domain.usecases.CreateWeekFifteenIncrements
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val createWeekFifteenIncrements: CreateWeekFifteenIncrements
): ViewModel() {

    var timeSlots by mutableStateOf(emptyList<FifteenMinSlot>())
    val dayList = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    init{
        timeSlots = createWeekFifteenIncrements()
    }
}