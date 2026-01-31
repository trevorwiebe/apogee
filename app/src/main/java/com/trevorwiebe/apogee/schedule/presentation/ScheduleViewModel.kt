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

    fun onEvent(event: ScheduleEvents){
        when(event){
            is ScheduleEvents.OnClick -> handleClick(event)
            is ScheduleEvents.OnLongClick -> handleLongClick(event)
            is ScheduleEvents.OnSaveEvent -> handleSaveEvent(event)
            is ScheduleEvents.OnSaveScheduleCould -> handleSaveScheduleCould(event)
            is ScheduleEvents.OnDeselectAll -> handleDeselectAll()
        }
    }

    private fun handleClick(event: ScheduleEvents.OnClick){
        if(anySelected){
            updateTimeSlotSelection(event.timeSlot)
        }
        setAnySelected()
    }

    private fun handleLongClick(event: ScheduleEvents.OnLongClick) {
        updateTimeSlotSelection(event.timeSlot)
        setAnySelected()
    }

    private fun handleSaveEvent(event: ScheduleEvents.OnSaveEvent){
        val selectedSlots = timeSlots.filter { it.selected }
        if (selectedSlots.isEmpty()) return

        val scheduleShouldItems = selectedSlots.map { uiTimeSlot ->
            ScheduleShould(
                id = 0,
                scheduleCouldId = event.scheduleCouldId,
                startTime = uiTimeSlot.slot.startTime,
                endTime = uiTimeSlot.slot.endTime,
                dayOfWeek = weekDaySelected,
            )
        }

        viewModelScope.launch {
            scheduleShouldItems.forEach { saveScheduleShould(it) }
            timeSlots = timeSlots.map { it.copy(selected = false) }
            anySelected = false
        }
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

    private fun handleDeselectAll(){
        timeSlots = timeSlots.map { it.copy(selected = false) }
        anySelected = false
    }

    private fun updateTimeSlotSelection(timeSlot: UiTimeSlot){
        timeSlots = timeSlots.map {
            if (it == timeSlot) {
                it.copy(selected = !it.selected)
            } else {
                it
            }
        }
    }

    private fun setAnySelected(){
        anySelected = timeSlots.any { it.selected }
    }

}