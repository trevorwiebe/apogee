package com.trevorwiebe.apogee.logtime.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.apogee.logtime.data.Did
import com.trevorwiebe.apogee.logtime.domain.usecases.CreateDateTimeSlots
import com.trevorwiebe.apogee.logtime.domain.usecases.GetDid
import com.trevorwiebe.apogee.logtime.domain.usecases.SaveDid
import com.trevorwiebe.apogee.schedule.data.ScheduleCould
import com.trevorwiebe.apogee.schedule.domain.usecases.GetScheduleCould
import com.trevorwiebe.apogee.schedule.domain.usecases.GetScheduledShould
import com.trevorwiebe.apogee.schedule.presentation.ScheduleVMUtils.lightenColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class LogTimeViewModel @Inject constructor(
    private val createDateTimeSlots: CreateDateTimeSlots,
    private val getScheduledShould: GetScheduledShould,
    private val getScheduleCould: GetScheduleCould,
    private val getDid: GetDid,
    private val saveDid: SaveDid
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

    var scheduleCouldList by mutableStateOf<List<ScheduleCould>>(emptyList())
        private set

    private val _scrollToNowEvent = Channel<Int>()
    val scrollToNowEvent = _scrollToNowEvent.receiveAsFlow()

    private var scheduleShouldList: List<com.trevorwiebe.apogee.schedule.data.ScheduleShould> = emptyList()
    private var didList: List<Did> = emptyList()

    private var loadedStartDate: LocalDate = LocalDate.now()
    private var loadedEndDate: LocalDate = LocalDate.now()

    init {
        initializeSlots()
        observeSchedules()
    }

    private fun initializeSlots() {
        val today = LocalDate.now()
        val now = LocalDateTime.now()

        loadedStartDate = today.minusDays(INITIAL_DAYS_BEFORE.toLong())
        loadedEndDate = today.plusDays(INITIAL_DAYS_AFTER.toLong())

        val dateTimeSlots = createDateTimeSlots.forRange(loadedStartDate, loadedEndDate)
        slots = dateTimeSlots.map { LogTimeUiSlot(slot = it) }

        val daysFromStart = ChronoUnit.DAYS.between(loadedStartDate, today).toInt()
        val slotIndexInDay = createDateTimeSlots.slotIndexForTime(now)
        val initialIndex = (daysFromStart * 97) + slotIndexInDay

        state = state.copy(
            initialScrollIndex = initialIndex.coerceIn(0, slots.size - 1)
        )
    }

    private fun observeSchedules() {
        viewModelScope.launch {
            combine(
                getScheduledShould(),
                getScheduleCould(),
                getDid()
            ) { shouldList, couldList, didEntries ->
                scheduleShouldList = shouldList
                scheduleCouldList = couldList
                didList = didEntries
                mapSchedulesToSlots(slots)
            }.collect { updatedSlots ->
                slots = updatedSlots
            }
        }
    }

    private fun mapSchedulesToSlots(currentSlots: List<LogTimeUiSlot>): List<LogTimeUiSlot> {
        return currentSlots.map { uiSlot ->
            // Map scheduled (should) activity
            val matchingTask = scheduleShouldList.find { task ->
                task.dayOfWeek == uiSlot.slot.zeroDayOfWeek &&
                task.startTime == uiSlot.slot.startTime
            }
            val scheduleCould = matchingTask?.let { task ->
                scheduleCouldList.find { it.id == task.scheduleCouldId }
            }

            // Map actual (did) activity
            val matchingDid = didList.find { did ->
                did.startDateTime == uiSlot.slot.startDateTime
            }
            val actualCould = matchingDid?.let { did ->
                scheduleCouldList.find { it.id == did.scheduleCouldId }
            }

            uiSlot.copy(
                scheduledName = scheduleCould?.name,
                color = scheduleCould?.color,
                lightColor = scheduleCould?.let { lightenColor(it.color) },
                actualName = actualCould?.name,
                actualColor = actualCould?.color,
                actualLightColor = actualCould?.let { lightenColor(it.color) }
            )
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
                val isNowVisible = state.initialScrollIndex in event.firstVisibleIndex..event.lastVisibleIndex
                if (state.showScrollToNowButton != !isNowVisible) {
                    state = state.copy(showScrollToNowButton = !isNowVisible)
                }
            }
            is LogTimeEvents.OnScrollToNowClicked -> {
                viewModelScope.launch {
                    _scrollToNowEvent.send(state.initialScrollIndex)
                }
            }
            is LogTimeEvents.OnClick -> {
                toggleSelection(event.slot)
            }
            is LogTimeEvents.OnSaveActual -> {
                saveActualForSelected(event.scheduleCouldId)
            }
            is LogTimeEvents.OnDeselectAll -> {
                deselectAll()
            }
        }
    }

    private fun toggleSelection(slot: LogTimeUiSlot) {
        slots = slots.map {
            if (it.slot.startDateTime == slot.slot.startDateTime) {
                it.copy(selected = !it.selected)
            } else it
        }
        updateAnySelected()
    }

    private fun deselectAll() {
        slots = slots.map { it.copy(selected = false) }
        state = state.copy(anySelected = false)
    }

    private fun updateAnySelected() {
        val anySelected = slots.any { it.selected }
        state = state.copy(anySelected = anySelected)
    }

    private fun saveActualForSelected(scheduleCouldId: Int) {
        viewModelScope.launch {
            val selectedSlots = slots.filter { it.selected }
            selectedSlots.forEach { slot ->
                val did = Did(
                    scheduleCouldId = scheduleCouldId,
                    startDateTime = slot.slot.startDateTime
                )
                saveDid(did)
            }
            deselectAll()
        }
    }

    private fun loadEarlierDates() {
        val newStartDate = loadedStartDate.minusDays(LOAD_THRESHOLD_DAYS.toLong())
        val newSlots = createDateTimeSlots.forRange(newStartDate, loadedStartDate.minusDays(1))
            .map { LogTimeUiSlot(slot = it) }

        loadedStartDate = newStartDate
        slots = mapSchedulesToSlots(newSlots) + slots
    }

    private fun loadLaterDates() {
        val newEndDate = loadedEndDate.plusDays(LOAD_THRESHOLD_DAYS.toLong())
        val newSlots = createDateTimeSlots.forRange(loadedEndDate.plusDays(1), newEndDate)
            .map { LogTimeUiSlot(slot = it) }

        loadedEndDate = newEndDate
        slots = slots + mapSchedulesToSlots(newSlots)
    }
}
