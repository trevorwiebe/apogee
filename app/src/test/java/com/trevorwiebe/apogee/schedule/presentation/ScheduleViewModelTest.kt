package com.trevorwiebe.apogee.schedule.presentation

import com.trevorwiebe.apogee.global.domain.usecases.CreateWeekFifteenIncrements
import com.trevorwiebe.apogee.schedule.data.ScheduleShould
import com.trevorwiebe.apogee.schedule.domain.usecases.GetScheduledShould
import com.trevorwiebe.apogee.schedule.domain.usecases.SaveScheduleShould
import com.trevorwiebe.apogee.testutils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.slot
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
    private lateinit var getScheduledShould: GetScheduledShould
    private lateinit var viewModel: ScheduleViewModel

    @Before
    fun setup() {
        saveScheduleShould = mockk()
        getScheduledShould = mockk()

        every { getScheduledShould() } returns flowOf(emptyList())
        coEvery { saveScheduleShould(any()) } just Runs
    }

    private fun createViewModel(): ScheduleViewModel {
        return ScheduleViewModel(
            createWeekFifteenIncrements,
            saveScheduleShould,
            getScheduledShould
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
    fun `init sets saveButtonEnabled to false`() {
        // When
        viewModel = createViewModel()

        // Then
        assertFalse(viewModel.saveButtonEnabled)
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
        val scheduledItem = ScheduleShould(
            id = 1,
            name = "Test Task",
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(9, 14, 59, 999_999_999),
            dayOfWeek = 0
        )
        every { getScheduledShould() } returns flowOf(listOf(scheduledItem))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val slotAt9AM = viewModel.timeSlots.find {
            it.slot.startTime == LocalTime.of(9, 0) && it.slot.dayOfTheWeek == 0
        }
        assertNotNull(slotAt9AM?.task)
        assertEquals("Test Task", slotAt9AM?.task?.name)
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

    // region Selection Validation Tests

    @Test
    fun `saveButtonEnabled is true when single slot selected`() {
        // Given
        viewModel = createViewModel()

        // When
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))

        // Then
        assertTrue(viewModel.saveButtonEnabled)
    }

    @Test
    fun `saveButtonEnabled is true when consecutive slots selected`() {
        // Given
        viewModel = createViewModel()

        // When - select 3 consecutive slots
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))
        viewModel.onEvent(ScheduleEvents.OnClick(viewModel.timeSlots[1]))
        viewModel.onEvent(ScheduleEvents.OnClick(viewModel.timeSlots[2]))

        // Then
        assertTrue(viewModel.saveButtonEnabled)
    }

    @Test
    fun `saveButtonEnabled is false when non-consecutive slots selected`() {
        // Given
        viewModel = createViewModel()

        // When - select slot 0 and slot 2 (skipping slot 1)
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))
        viewModel.onEvent(ScheduleEvents.OnClick(viewModel.timeSlots[2]))

        // Then
        assertFalse(viewModel.saveButtonEnabled)
    }

    @Test
    fun `saveButtonEnabled is false when no slots selected`() {
        // Given
        viewModel = createViewModel()

        // Then
        assertFalse(viewModel.saveButtonEnabled)
    }

    // endregion

    // region OnSaveEvent Tests

    @Test
    fun `OnSaveEvent saves schedule with correct times`() = runTest {
        // Given
        viewModel = createViewModel()
        val capturedSchedule = slot<ScheduleShould>()
        coEvery { saveScheduleShould(capture(capturedSchedule)) } just Runs

        // Select first two slots (00:00 - 00:30)
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))
        viewModel.onEvent(ScheduleEvents.OnClick(viewModel.timeSlots[1]))

        // When
        viewModel.onEvent(ScheduleEvents.OnSaveEvent("My Task"))
        advanceUntilIdle()

        // Then
        coVerify { saveScheduleShould(any()) }
        assertEquals("My Task", capturedSchedule.captured.name)
        assertEquals(LocalTime.of(0, 0), capturedSchedule.captured.startTime)
        assertEquals(LocalTime.of(0, 29, 59, 999_999_999), capturedSchedule.captured.endTime)
    }

    @Test
    fun `OnSaveEvent uses Task as default title when empty`() = runTest {
        // Given
        viewModel = createViewModel()
        val capturedSchedule = slot<ScheduleShould>()
        coEvery { saveScheduleShould(capture(capturedSchedule)) } just Runs

        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))

        // When
        viewModel.onEvent(ScheduleEvents.OnSaveEvent(""))
        advanceUntilIdle()

        // Then
        coVerify { saveScheduleShould(any()) }
        assertEquals("Task", capturedSchedule.captured.name)
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
        viewModel.onEvent(ScheduleEvents.OnSaveEvent("Task"))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.timeSlots.none { it.selected })
        assertFalse(viewModel.anySelected)
    }

    @Test
    fun `OnSaveEvent uses current weekDaySelected`() = runTest {
        // Given
        viewModel = createViewModel()
        val capturedSchedule = slot<ScheduleShould>()
        coEvery { saveScheduleShould(capture(capturedSchedule)) } just Runs

        // Note: weekDaySelected defaults to 0, we'd need a setter or event to change it
        // For now, test with default value
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))

        // When
        viewModel.onEvent(ScheduleEvents.OnSaveEvent("Task"))
        advanceUntilIdle()

        // Then
        assertEquals(0, capturedSchedule.captured.dayOfWeek)
    }

    @Test
    fun `OnSaveEvent does not save when slots have gaps`() = runTest {
        // Given
        viewModel = createViewModel()
        // Select non-consecutive slots
        viewModel.onEvent(ScheduleEvents.OnLongClick(viewModel.timeSlots[0]))
        viewModel.onEvent(ScheduleEvents.OnClick(viewModel.timeSlots[2])) // Gap at slot 1
        assertFalse(viewModel.saveButtonEnabled)

        // When
        viewModel.onEvent(ScheduleEvents.OnSaveEvent("Task"))
        advanceUntilIdle()

        // Then - save should not be called due to exception
        coVerify(exactly = 0) { saveScheduleShould(any()) }
    }

    // endregion

    // region Data Loading Tests

    @Test
    fun `scheduled items are mapped to correct time slots`() = runTest {
        // Given
        val scheduledItems = listOf(
            ScheduleShould(
                id = 1,
                name = "Morning Meeting",
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(9, 59, 59, 999_999_999),
                dayOfWeek = 0
            )
        )
        every { getScheduledShould() } returns flowOf(scheduledItems)

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
        assertTrue(slots9to10.all { it.task?.name == "Morning Meeting" })
    }

    @Test
    fun `time slots without scheduled items have null task`() = runTest {
        // Given
        val scheduledItem = ScheduleShould(
            id = 1,
            name = "Meeting",
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(9, 14, 59, 999_999_999),
            dayOfWeek = 0
        )
        every { getScheduledShould() } returns flowOf(listOf(scheduledItem))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then - slots outside the scheduled time should have null task
        val slot8AM = viewModel.timeSlots.find {
            it.slot.startTime == LocalTime.of(8, 0) && it.slot.dayOfTheWeek == 0
        }
        assertNull(slot8AM?.task)
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
