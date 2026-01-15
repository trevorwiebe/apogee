package com.trevorwiebe.apogee.schedule.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.trevorwiebe.apogee.global.domain.usecases.CreateWeekFifteenIncrements
import com.trevorwiebe.apogee.schedule.data.ScheduleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val createWeekFifteenIncrements: CreateWeekFifteenIncrements
): ViewModel() {

    var timeSlots by mutableStateOf(emptyList<UiTimeSlot>())
    var anySelected by mutableStateOf(false)
    var weekDaySelected by mutableIntStateOf(0)
    var saveButtonEnabled by mutableStateOf(false)
    val dayList = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    init{
        timeSlots = createWeekFifteenIncrements().map {
            UiTimeSlot(
                slot = it,
                selected = false
            )
        }
    }

    fun onEvent(event: ScheduleEvents){
        when(event){
            is ScheduleEvents.OnSaveEvent -> {
                try{
                    val title = event.title
                    val times = calculateStartAndEnd(timeSlots)
                    val scheduleItem = ScheduleItem(
                        id = 0,
                        name = title,
                        startTime = times.first,
                        endTime = times.second,
                        dayOfWeek = weekDaySelected,
                    )


                }catch (e: Exception){ }
            }
        }
    }

    fun onTimeSlotClicked(timeSlot: UiTimeSlot) {
        if(anySelected){
            updateTimeSlotSelection(timeSlot)
        }
        setAnySelected()
    }
    fun onTimeSlotLongClicked(timeSlot: UiTimeSlot) {
        updateTimeSlotSelection(timeSlot)
        setAnySelected()
    }

    private fun updateTimeSlotSelection(timeSlot: UiTimeSlot){
        timeSlots = timeSlots.map {
            if (it == timeSlot) {
                it.copy(selected = !it.selected)
            } else {
                it
            }
        }

        saveButtonEnabled = try{
            calculateStartAndEnd(timeSlots)
            true
        }catch (e: Exception){
            false
        }
    }

    fun setAnySelected(){
        anySelected = timeSlots.any { it.selected }
    }

    private fun calculateStartAndEnd(list: List<UiTimeSlot>): Pair<LocalTime, LocalTime> {
        val sortedSlots = list.filter { it.selected }.sortedBy { it.slot.startTime }

        if (sortedSlots.isEmpty()) {
            throw Exception("No time slots selected")
        }

        var previousStartMinutes: Int? = null

        sortedSlots.forEach { uiSlot ->
            val slot = uiSlot.slot
            val currentStartMinutes = slot.startTime.hour * 60 + slot.startTime.minute

            if (previousStartMinutes != null && currentStartMinutes != previousStartMinutes + 15) {
                throw Exception("There is a time gap")
            }

            previousStartMinutes = currentStartMinutes
        }

        val start = sortedSlots.first().slot.startTime
        val end = sortedSlots.last().slot.endTime

        return start to end
    }
}