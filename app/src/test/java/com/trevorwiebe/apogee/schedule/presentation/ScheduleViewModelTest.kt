package com.trevorwiebe.apogee.schedule.presentation

import com.trevorwiebe.apogee.global.domain.usecases.CreateWeekFifteenIncrements
import com.trevorwiebe.apogee.schedule.data.ScheduleShould
import com.trevorwiebe.apogee.schedule.domain.usecases.GetScheduleCould
import com.trevorwiebe.apogee.schedule.domain.usecases.GetScheduledShould
import com.trevorwiebe.apogee.schedule.domain.usecases.SaveScheduleCould
import com.trevorwiebe.apogee.schedule.domain.usecases.SaveScheduleShould
import com.trevorwiebe.apogee.testutils.MainDispatcherRule
import com.trevorwiebe.apogee.testutils.TestDataFactory.createScheduleCould
import com.trevorwiebe.apogee.testutils.TestDataFactory.createScheduleShould
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val createWeekFifteenIncrements = CreateWeekFifteenIncrements()
    private lateinit var saveScheduleShould: SaveScheduleShould
    private lateinit var saveScheduleCould: SaveScheduleCould
    private lateinit var getScheduledShould: GetScheduledShould
    private lateinit var getScheduleCould: GetScheduleCould
    private lateinit var viewModel: ScheduleViewModel

    @Before
    fun setup() {
        saveScheduleShould = mockk()
        saveScheduleCould = mockk()
        getScheduledShould = mockk()
        getScheduleCould = mockk()

        every { getScheduledShould() } returns flowOf(emptyList())
        every { getScheduleCould() } returns flowOf(emptyList())
        coEvery { saveScheduleShould(any()) } just Runs
        coEvery { saveScheduleCould(any()) } just Runs
    }

    private fun createViewModel(): ScheduleViewModel {
        return ScheduleViewModel(
            createWeekFifteenIncrements,
            saveScheduleShould,
            saveScheduleCould,
            getScheduledShould,
            getScheduleCould
        )
    }

    // region Initialization Tests

    @Test
    fun `init creates 672 time slots from CreateWeekFifteenIncrements`() {
        // When
        viewModel = createViewModel()

        // Then
        assertEquals(672, viewModel.timeSlots.size)
    }

    @Test
    fun `init sets all time slots as not selected`() {
        // When
        viewModel = createViewModel()

        // Then
        assertTrue(viewModel.timeSlots.all { !it.selected })
    }

    @Test
    fun `init sets anySelected to false`() {
        // When
        viewModel = createViewModel()

        // Then
        assertFalse(viewModel.anySelected)
    }

    @Test
    fun `init sets weekDaySelected to 0`() {
        // When
        viewModel = createViewModel()

        // Then
        assertEquals(0, viewModel.weekDaySelected)
    }

    @Test
    fun `init loads scheduled items from getScheduledShould`() = runTest {
        // Given
        val scheduleCould = createScheduleCould(name = "Test Task")
        val scheduleShould = createScheduleShould(dayOfWeek = 0)
        every { getScheduleCould() } returns flowOf(listOf(scheduleCould))
        every { getScheduledShould() } returns flowOf(listOf(scheduleShould))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val slotAt9AM = viewModel.timeSlots.find {
            it.slot.startTime == LocalTime.of(9, 0) && it.slot.dayOfTheWeek == 0
        }
        assertNotNull(slotAt9AM?.taskName)
        assertEquals("Test Task", slotAt9AM?.taskName)
    }

    // endregion

    // region OnLongClick Tests

    @Test
    fun `OnLongClick selects unselected time slot`() {
        // Given
        viewModel = createViewModel()
        val timeSlot = viewModel.timeSlots[0]
        assertFalse(timeSlot.selected)

        // When
        viewModel.onEvent(ScheduleEvents.OnLongClick(timeSlot))

        // Then
        assertTrue(viewModel.timeSlots[0].selected)
    }

    @Test
    fun `OnLongClick deselects selected time slot`() {
        // Given
        viewModel = createViewModel()
        val timeSlot = viewModel.timeSlots[0]

        // First, select the slot
        viewModel.onEvent(ScheduleEvents.OnLongClick(timeSlot))
        assertTrue(viewModel.timeSlots[0].selected)

        // When - long click again to deselect
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))

        // Then
        assertFalse(viewModel.timeSlots[0].selected)
    }

    @Test
    fun `OnLongClick sets anySelected to true when slot selected`() {
        // Given
        viewModel = createViewModel()
        assertFalse(viewModel.anySelected)

        // When
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))

        // Then
        assertTrue(viewModel.anySelected)
    }

    @Test
    fun `OnLongClick sets anySelected to false when all slots deselected`() {
        // Given
        viewModel = createViewModel()
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))
        assertTrue(viewModel.anySelected)

        // When - deselect the slot
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))

        // Then
        assertFalse(viewModel.anySelected)
    }

    // endregion

    // region OnClick Tests

    @Test
    fun `OnClick does nothing when anySelected is false`() {
        // Given
        viewModel = createViewModel()
        val timeSlot = viewModel.timeSlots[0]
        assertFalse(viewModel.anySelected)
        assertFalse(timeSlot.selected)

        // When
        viewModel.onEvent(ScheduleEvents.OnClick(timeSlot))

        // Then
        assertFalse(viewModel.timeSlots[0].selected)
        assertFalse(viewModel.anySelected)
    }

    @Test
    fun `OnClick toggles selection when anySelected is true`() {
        // Given
        viewModel = createViewModel()
        // First select a slot to make anySelected true
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))
        assertTrue(viewModel.anySelected)

        // When - click on a different slot
        val secondSlot = viewModel.timeSlots[1]
        viewModel.onEvent(ScheduleEvents.OnClick(secondSlot))

        // Then
        assertTrue(viewModel.timeSlots[1].selected)
    }

    @Test
    fun `OnClick can deselect when anySelected is true`() {
        // Given
        viewModel = createViewModel()
        // Select two slots
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))
        viewModel.onEvent(ScheduleEvents.OnClick(viewModel.timeSlots[1]))
        assertTrue(viewModel.timeSlots[0].selected)
        assertTrue(viewModel.timeSlots[1].selected)

        // When - click to deselect first slot
        viewModel.onEvent(ScheduleEvents.OnClick(viewModel.timeSlots[0]))

        // Then
        assertFalse(viewModel.timeSlots[0].selected)
        assertTrue(viewModel.timeSlots[1].selected)
    }

    // endregion

    // region OnSaveEvent Tests

    @Test
    fun `OnSaveEvent saves individual schedule for each selected slot`() = runTest {
        // Given
        viewModel = createViewModel()
        val capturedSchedules = mutableListOf<ScheduleShould>()
        coEvery { saveScheduleShould(capture(capturedSchedules)) } just Runs

        // Select first two slots (00:00-00:15 and 00:15-00:30)
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))
        viewModel.onEvent(ScheduleEvents.OnClick(viewModel.timeSlots[1]))

        // When
        viewModel.onEvent(ScheduleEvents.OnSaveEvent(100))
        advanceUntilIdle()

        // Then - should save two individual schedules
        coVerify(exactly = 2) { saveScheduleShould(any()) }
        assertEquals(2, capturedSchedules.size)

        // First slot: 00:00-00:15
        assertEquals(100, capturedSchedules[0].scheduleCouldId)
        assertEquals(LocalTime.of(0, 0), capturedSchedules[0].startTime)
        assertEquals(LocalTime.of(0, 14, 59, 999_999_999), capturedSchedules[0].endTime)

        // Second slot: 00:15-00:30
        assertEquals(100, capturedSchedules[1].scheduleCouldId)
        assertEquals(LocalTime.of(0, 15), capturedSchedules[1].startTime)
        assertEquals(LocalTime.of(0, 29, 59, 999_999_999), capturedSchedules[1].endTime)
    }

    @Test
    fun `OnSaveEvent clears selection after save`() = runTest {
        // Given
        viewModel = createViewModel()
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))
        viewModel.onEvent(ScheduleEvents.OnClick(viewModel.timeSlots[1]))
        assertTrue(viewModel.timeSlots[0].selected)
        assertTrue(viewModel.timeSlots[1].selected)

        // When
        viewModel.onEvent(ScheduleEvents.OnSaveEvent(100))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.timeSlots.none { it.selected })
        assertFalse(viewModel.anySelected)
    }

    @Test
    fun `OnSaveEvent uses current weekDaySelected`() = runTest {
        // Given
        viewModel = createViewModel()
        val capturedSchedules = mutableListOf<ScheduleShould>()
        coEvery { saveScheduleShould(capture(capturedSchedules)) } just Runs

        // Note: weekDaySelected defaults to 0, we'd need a setter or event to change it
        // For now, test with default value
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))

        // When
        viewModel.onEvent(ScheduleEvents.OnSaveEvent(100))
        advanceUntilIdle()

        // Then
        assertEquals(0, capturedSchedules[0].dayOfWeek)
    }

    @Test
    fun `OnSaveEvent saves non-consecutive slots as individual schedules`() = runTest {
        // Given
        viewModel = createViewModel()
        val capturedSchedules = mutableListOf<ScheduleShould>()
        coEvery { saveScheduleShould(capture(capturedSchedules)) } just Runs

        // Select non-consecutive slots (slot 0 and slot 2, skipping slot 1)
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))
        viewModel.onEvent(ScheduleEvents.OnClick(viewModel.timeSlots[2]))

        // When
        viewModel.onEvent(ScheduleEvents.OnSaveEvent(100))
        advanceUntilIdle()

        // Then - both slots should be saved as individual schedules
        coVerify(exactly = 2) { saveScheduleShould(any()) }
        assertEquals(2, capturedSchedules.size)
    }

    // endregion

    // region Data Loading Tests

    @Test
    fun `scheduled items are mapped to correct time slots`() = runTest {
        // Given
        val scheduleCould = createScheduleCould(name = "Morning Meeting")
        val scheduleShould = createScheduleShould(
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(9, 59, 59, 999_999_999),
            dayOfWeek = 0
        )
        every { getScheduleCould() } returns flowOf(listOf(scheduleCould))
        every { getScheduledShould() } returns flowOf(listOf(scheduleShould))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then - slots from 9:00-10:00 should have the task
        val slots9to10 = viewModel.timeSlots.filter {
            it.slot.dayOfTheWeek == 0 &&
            it.slot.startTime >= LocalTime.of(9, 0) &&
            it.slot.startTime < LocalTime.of(10, 0)
        }

        assertTrue(slots9to10.isNotEmpty())
        assertTrue(slots9to10.all { it.taskName == "Morning Meeting" })
    }

    @Test
    fun `time slots without scheduled items have null taskName`() = runTest {
        // Given
        val scheduleCould = createScheduleCould(name = "Meeting")
        val scheduleShould = createScheduleShould(dayOfWeek = 0)
        every { getScheduleCould() } returns flowOf(listOf(scheduleCould))
        every { getScheduledShould() } returns flowOf(listOf(scheduleShould))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then - slots outside the scheduled time should have null taskName
        val slot8AM = viewModel.timeSlots.find {
            it.slot.startTime == LocalTime.of(8, 0) && it.slot.dayOfTheWeek == 0
        }
        assertNull(slot8AM?.taskName)
    }

    // endregion

    // region Day List Tests

    @Test
    fun `dayList contains all seven days`() {
        // When
        viewModel = createViewModel()

        // Then
        assertEquals(7, viewModel.dayList.size)
        assertEquals(listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"), viewModel.dayList)
    }

    // endregion
}
