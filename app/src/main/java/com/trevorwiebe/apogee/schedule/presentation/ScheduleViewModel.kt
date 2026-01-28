package com.trevorwiebe.apogee.schedule.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.apogee.global.domain.usecases.CreateWeekFifteenIncrements
import com.trevorwiebe.apogee.schedule.data.ScheduleCould
import com.trevorwiebe.apogee.schedule.data.ScheduleShould
import com.trevorwiebe.apogee.schedule.domain.usecases.GetScheduleCould
import com.trevorwiebe.apogee.schedule.domain.usecases.GetScheduledShould
import com.trevorwiebe.apogee.schedule.domain.usecases.SaveScheduleCould
import com.trevorwiebe.apogee.schedule.domain.usecases.SaveScheduleShould
import com.trevorwiebe.apogee.schedule.presentation.ScheduleVMUtils.mapTaskToTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val createWeekFifteenIncrements: CreateWeekFifteenIncrements,
    private val saveScheduleShould: SaveScheduleShould,
    private val saveScheduleCould: SaveScheduleCould,
    private val getScheduledShould: GetScheduledShould,
    private val getScheduleCould: GetScheduleCould
): ViewModel() {

    var timeSlots by mutableStateOf(emptyList<UiTimeSlot>())
    var anySelected by mutableStateOf(false)
    var weekDaySelected by mutableIntStateOf(0)
    var saveButtonEnabled by mutableStateOf(false)
    var scheduleShouldList by mutableStateOf(emptyList<ScheduleShould>())
    var scheduleCouldList by mutableStateOf(emptyList<ScheduleCould>())
    val dayList = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    private val _snackbarEvent = Channel<String>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    init{
        timeSlots = createWeekFifteenIncrements().map {
            UiTimeSlot(
                slot = it,
                selected = false,
                taskName = null
            )
        }
        loadData()
    }

    fun onEvent(event: ScheduleEvents){
        when(event){
            is ScheduleEvents.OnClick -> {
                if(anySelected){
                    updateTimeSlotSelection(event.timeSlot)
                }
                setAnySelected()
            }
            is ScheduleEvents.OnLongClick -> {
                updateTimeSlotSelection(event.timeSlot)
                setAnySelected()
            }
            is ScheduleEvents.OnSaveEvent -> {
                try{
                    val times = calculateStartAndEnd(timeSlots)
                    val scheduleShould = ScheduleShould(
                        id = 0,
                        scheduleCouldId = event.scheduleCouldId,
                        startTime = times.first,
                        endTime = times.second,
                        dayOfWeek = weekDaySelected,
                    )

                    viewModelScope.launch {
                        saveScheduleShould(scheduleShould)
                        timeSlots = timeSlots.map { it.copy(selected = false) }
                        anySelected = false
                    }

                }catch (e: Exception){ }
            }
            is ScheduleEvents.OnSaveScheduleCould -> handleSaveScheduleCould(event)
            is ScheduleEvents.OnDeselectAll -> {
                timeSlots = timeSlots.map { it.copy(selected = false) }
                anySelected = false
            }
        }
    }

    private fun loadData(){
        viewModelScope.launch {
            combine(
                getScheduledShould(),
                getScheduleCould()
            ) { shouldList, couldList ->
                scheduleShouldList = shouldList
                scheduleCouldList = couldList
                mapTaskToTime(timeSlots, shouldList, couldList)
            }.collect { mappedSlots ->
                timeSlots = mappedSlots
            }
        }
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

    private fun setAnySelected(){
        anySelected = timeSlots.any { it.selected }
    }

    private fun handleSaveScheduleCould(event: ScheduleEvents.OnSaveScheduleCould){
        val scheduleCould = ScheduleCould(
            id = 0,
            name = event.title,
            description = event.description,
            color = event.color
        )
        viewModelScope.launch {
            saveScheduleCould(scheduleCould)
            _snackbarEvent.send("Activity saved")
        }
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