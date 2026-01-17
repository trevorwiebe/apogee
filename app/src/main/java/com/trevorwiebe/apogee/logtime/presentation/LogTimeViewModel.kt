package com.trevorwiebe.apogee.logtime.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.apogee.logtime.domain.usecases.CreateDateTimeSlots
import com.trevorwiebe.apogee.schedule.data.ScheduleShould
import com.trevorwiebe.apogee.schedule.domain.usecases.GetScheduledShould
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class LogTimeViewModel @Inject constructor(
    private val createDateTimeSlots: CreateDateTimeSlots,
    private val getScheduledShould: GetScheduledShould
) : ViewModel() {

    companion object {
        const val INITIAL_DAYS_BEFORE = 30
        const val INITIAL_DAYS_AFTER = 30
        const val LOAD_THRESHOLD_DAYS = 7
    }

    var state by mutableStateOf(LogTimeState())
        private set

    var slots by mutableStateOf<List<LogTimeUiSlot>>(emptyList())
        private set

    private var scheduleShouldList: List<ScheduleShould> = emptyList()

    private var loadedStartDate: LocalDate = LocalDate.now()
    private var loadedEndDate: LocalDate = LocalDate.now()

    init {
        initializeSlots()
        observeScheduleShould()
    }

    private fun initializeSlots() {
        val today = LocalDate.now()
        val now = LocalDateTime.now()

        loadedStartDate = today.minusDays(INITIAL_DAYS_BEFORE.toLong())
        loadedEndDate = today.plusDays(INITIAL_DAYS_AFTER.toLong())

        val dateTimeSlots = createDateTimeSlots.forRange(loadedStartDate, loadedEndDate)
        slots = dateTimeSlots.map { LogTimeUiSlot(slot = it, scheduleShould = null) }

        val daysFromStart = ChronoUnit.DAYS.between(loadedStartDate, today).toInt()
        val slotIndexInDay = createDateTimeSlots.slotIndexForTime(now)
        val initialIndex = (daysFromStart * 96) + slotIndexInDay

        state = state.copy(
            initialScrollIndex = initialIndex.coerceIn(0, slots.size - 1)
        )
    }

    private fun observeScheduleShould() {
        viewModelScope.launch {
            getScheduledShould().collect { list ->
                scheduleShouldList = list
                slots = mapScheduleShouldToSlots(slots)
            }
        }
    }

    private fun mapScheduleShouldToSlots(currentSlots: List<LogTimeUiSlot>): List<LogTimeUiSlot> {
        return currentSlots.map { uiSlot ->
            val matchingTask = scheduleShouldList.find { task ->
                task.dayOfWeek == uiSlot.slot.zeroDayOfWeek &&
                task.startTime <= uiSlot.slot.startTime &&
                task.endTime >= uiSlot.slot.endTime
            }
            uiSlot.copy(scheduleShould = matchingTask)
        }
    }

    fun onEvent(event: LogTimeEvents) {
        when (event) {
            is LogTimeEvents.OnScrollPositionChanged -> {
                if (event.firstVisibleIndex < LOAD_THRESHOLD_DAYS * 96) {
                    loadEarlierDates()
                }
                if (slots.size - event.lastVisibleIndex < LOAD_THRESHOLD_DAYS * 96) {
                    loadLaterDates()
                }
            }
        }
    }

    private fun loadEarlierDates() {
        val newStartDate = loadedStartDate.minusDays(LOAD_THRESHOLD_DAYS.toLong())
        val newSlots = createDateTimeSlots.forRange(newStartDate, loadedStartDate.minusDays(1))
            .map { LogTimeUiSlot(slot = it, scheduleShould = null) }

        loadedStartDate = newStartDate
        slots = mapScheduleShouldToSlots(newSlots) + slots
    }

    private fun loadLaterDates() {
        val newEndDate = loadedEndDate.plusDays(LOAD_THRESHOLD_DAYS.toLong())
        val newSlots = createDateTimeSlots.forRange(loadedEndDate.plusDays(1), newEndDate)
            .map { LogTimeUiSlot(slot = it, scheduleShould = null) }

        loadedEndDate = newEndDate
        slots = slots + mapScheduleShouldToSlots(newSlots)
    }
}
