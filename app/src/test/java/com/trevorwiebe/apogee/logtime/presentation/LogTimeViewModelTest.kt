package com.trevorwiebe.apogee.logtime.presentation

import com.trevorwiebe.apogee.logtime.domain.usecases.CreateDateTimeSlots
import com.trevorwiebe.apogee.schedule.data.ScheduleShould
import com.trevorwiebe.apogee.schedule.domain.usecases.GetScheduledShould
import com.trevorwiebe.apogee.testutils.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
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
class LogTimeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val createDateTimeSlots = CreateDateTimeSlots()
    private lateinit var getScheduledShould: GetScheduledShould
    private lateinit var viewModel: LogTimeViewModel

    @Before
    fun setup() {
        getScheduledShould = mockk()
        every { getScheduledShould() } returns flowOf(emptyList())
    }

    private fun createViewModel(): LogTimeViewModel {
        return LogTimeViewModel(
            createDateTimeSlots,
            getScheduledShould
        )
    }

    // region Initialization Tests

    @Test
    fun `init creates slots for 61 days`() {
        // When
        viewModel = createViewModel()

        // Then
        // 30 days before + today + 30 days after = 61 days
        // 61 days * 96 slots per day = 5856 slots
        assertEquals(5856, viewModel.slots.size)
    }

    @Test
    fun `init sets initialScrollIndex within valid range`() {
        // When
        viewModel = createViewModel()

        // Then
        assertTrue(viewModel.state.initialScrollIndex >= 0)
        assertTrue(viewModel.state.initialScrollIndex < viewModel.slots.size)
    }

    @Test
    fun `init sets initialScrollIndex approximately at day 30`() {
        // When
        viewModel = createViewModel()

        // Then
        // Day 30 (today) starts at index 30 * 96 = 2880
        // The actual index depends on current time, but should be in day 30's range
        val day30StartIndex = 30 * 96
        val day30EndIndex = 31 * 96 - 1
        assertTrue(
            "Initial scroll index should be in day 30's range",
            viewModel.state.initialScrollIndex in day30StartIndex..day30EndIndex
        )
    }

    @Test
    fun `init sets all scheduleShould to null initially`() {
        // When
        viewModel = createViewModel()

        // Then
        assertTrue(viewModel.slots.all { it.scheduleShould == null })
    }

    // endregion

    // region ScheduleShould Mapping Tests

    @Test
    fun `scheduled items are mapped to matching time slots`() = runTest {
        // Given
        val scheduledItem = ScheduleShould(
            id = 1,
            name = "Morning Meeting",
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(9, 14, 59, 999_999_999),
            dayOfWeek = 0 // Monday
        )
        every { getScheduledShould() } returns flowOf(listOf(scheduledItem))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val mondaySlots = viewModel.slots.filter { it.slot.zeroDayOfWeek == 0 }
        val slot9AM = mondaySlots.find { it.slot.startTime == LocalTime.of(9, 0) }

        assertNotNull(slot9AM)
        assertNotNull(slot9AM?.scheduleShould)
        assertEquals("Morning Meeting", slot9AM?.scheduleShould?.name)
    }

    @Test
    fun `scheduled items match across multiple weeks`() = runTest {
        // Given
        val scheduledItem = ScheduleShould(
            id = 1,
            name = "Weekly Meeting",
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(10, 29, 59, 999_999_999),
            dayOfWeek = 2 // Wednesday
        )
        every { getScheduledShould() } returns flowOf(listOf(scheduledItem))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val wednesdaySlots = viewModel.slots.filter { it.slot.zeroDayOfWeek == 2 }
        val matchingSlots = wednesdaySlots.filter {
            it.slot.startTime >= LocalTime.of(10, 0) &&
            it.slot.startTime < LocalTime.of(10, 30)
        }

        assertTrue(matchingSlots.isNotEmpty())
        assertTrue(matchingSlots.all { it.scheduleShould?.name == "Weekly Meeting" })
    }

    @Test
    fun `slots without matching scheduled items have null scheduleShould`() = runTest {
        // Given
        val scheduledItem = ScheduleShould(
            id = 1,
            name = "Meeting",
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(9, 14, 59, 999_999_999),
            dayOfWeek = 0 // Monday
        )
        every { getScheduledShould() } returns flowOf(listOf(scheduledItem))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val mondaySlots = viewModel.slots.filter { it.slot.zeroDayOfWeek == 0 }
        val slot8AM = mondaySlots.find { it.slot.startTime == LocalTime.of(8, 0) }

        assertNotNull(slot8AM)
        assertNull(slot8AM?.scheduleShould)
    }

    @Test
    fun `scheduled item spanning multiple slots maps to all covered slots`() = runTest {
        // Given
        val scheduledItem = ScheduleShould(
            id = 1,
            name = "Long Meeting",
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(14, 59, 59, 999_999_999),
            dayOfWeek = 4 // Friday
        )
        every { getScheduledShould() } returns flowOf(listOf(scheduledItem))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val fridaySlots = viewModel.slots.filter { it.slot.zeroDayOfWeek == 4 }
        val slots14to15 = fridaySlots.filter {
            it.slot.startTime >= LocalTime.of(14, 0) &&
            it.slot.startTime < LocalTime.of(15, 0)
        }

        // Each Friday has 4 slots between 14:00-15:00, across multiple Fridays
        assertTrue(slots14to15.size >= 4)
        assertTrue(slots14to15.size % 4 == 0) // Should be multiple of 4 (one set per Friday)
        assertTrue(slots14to15.all { it.scheduleShould?.name == "Long Meeting" })
    }

    // endregion

    // region Load Earlier Dates Tests

    @Test
    fun `loadEarlierDates adds 7 days of slots at beginning`() {
        // Given
        viewModel = createViewModel()
        val initialSize = viewModel.slots.size

        // When
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(0, 10))

        // Then
        val expectedNewSlots = 7 * 96
        assertEquals(initialSize + expectedNewSlots, viewModel.slots.size)
    }

    @Test
    fun `loadEarlierDates prepends slots chronologically`() {
        // Given
        viewModel = createViewModel()
        val firstSlotBefore = viewModel.slots.first()

        // When
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(0, 10))

        // Then
        val newFirstSlot = viewModel.slots.first()
        assertTrue(newFirstSlot.slot.startDateTime.isBefore(firstSlotBefore.slot.startDateTime))
    }

    @Test
    fun `loadEarlierDates applies scheduleShould mapping to new slots`() = runTest {
        // Given
        val scheduledItem = ScheduleShould(
            id = 1,
            name = "Daily Standup",
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(9, 14, 59, 999_999_999),
            dayOfWeek = 1 // Tuesday
        )
        every { getScheduledShould() } returns flowOf(listOf(scheduledItem))

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(0, 10))

        // Then
        val tuesdaySlots = viewModel.slots.filter { it.slot.zeroDayOfWeek == 1 }
        val matchingSlots = tuesdaySlots.filter {
            it.slot.startTime == LocalTime.of(9, 0)
        }

        assertTrue(matchingSlots.all { it.scheduleShould?.name == "Daily Standup" })
    }

    // endregion

    // region Load Later Dates Tests

    @Test
    fun `loadLaterDates adds 7 days of slots at end`() {
        // Given
        viewModel = createViewModel()
        val initialSize = viewModel.slots.size

        // When
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(viewModel.slots.size - 100, viewModel.slots.size - 1))

        // Then
        val expectedNewSlots = 7 * 96
        assertEquals(initialSize + expectedNewSlots, viewModel.slots.size)
    }

    @Test
    fun `loadLaterDates appends slots chronologically`() {
        // Given
        viewModel = createViewModel()
        val lastSlotBefore = viewModel.slots.last()

        // When
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(viewModel.slots.size - 100, viewModel.slots.size - 1))

        // Then
        val newLastSlot = viewModel.slots.last()
        assertTrue(newLastSlot.slot.startDateTime.isAfter(lastSlotBefore.slot.startDateTime))
    }

    @Test
    fun `loadLaterDates applies scheduleShould mapping to new slots`() = runTest {
        // Given
        val scheduledItem = ScheduleShould(
            id = 1,
            name = "Weekly Review",
            startTime = LocalTime.of(16, 0),
            endTime = LocalTime.of(16, 59, 59, 999_999_999),
            dayOfWeek = 5 // Saturday
        )
        every { getScheduledShould() } returns flowOf(listOf(scheduledItem))

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(viewModel.slots.size - 100, viewModel.slots.size - 1))

        // Then
        val saturdaySlots = viewModel.slots.filter { it.slot.zeroDayOfWeek == 5 }
        val matchingSlots = saturdaySlots.filter {
            it.slot.startTime >= LocalTime.of(16, 0) &&
            it.slot.startTime < LocalTime.of(17, 0)
        }

        assertTrue(matchingSlots.all { it.scheduleShould?.name == "Weekly Review" })
    }

    // endregion

    // region onScrollPositionChanged Tests

    @Test
    fun `onScrollPositionChanged loads earlier dates when near start`() {
        // Given
        viewModel = createViewModel()
        val initialSize = viewModel.slots.size

        // When - scroll position is within threshold of start
        val thresholdIndex = (7 * 96) - 1 // Just within the 7-day threshold
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(thresholdIndex, thresholdIndex + 10))

        // Then
        assertTrue(viewModel.slots.size > initialSize)
    }

    @Test
    fun `onScrollPositionChanged loads later dates when near end`() {
        // Given
        viewModel = createViewModel()
        val initialSize = viewModel.slots.size

        // When - scroll position is within threshold of end
        val nearEndIndex = initialSize - (7 * 96) + 1
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(nearEndIndex - 10, nearEndIndex))

        // Then
        assertTrue(viewModel.slots.size > initialSize)
    }

    @Test
    fun `onScrollPositionChanged does not load when in middle`() {
        // Given
        viewModel = createViewModel()
        val initialSize = viewModel.slots.size

        // When - scroll position is in the middle
        val middleIndex = initialSize / 2
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(middleIndex, middleIndex + 10))

        // Then
        assertEquals(initialSize, viewModel.slots.size)
    }

    @Test
    fun `onScrollPositionChanged can trigger both loads if at extreme positions`() {
        // Given
        viewModel = createViewModel()
        val initialSize = viewModel.slots.size

        // First, load earlier to make room at start
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(0, 10))
        val sizeAfterEarlier = viewModel.slots.size

        // When - position near start (this shouldn't trigger since we just loaded)
        // But position near end should trigger
        val nearEndIndex = sizeAfterEarlier - (7 * 96) + 1
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(100, nearEndIndex))

        // Then - should have loaded more at the end
        assertTrue(viewModel.slots.size > sizeAfterEarlier)
    }

    // endregion

    // region Slots Order Tests

    @Test
    fun `slots are always in chronological order after initialization`() {
        // When
        viewModel = createViewModel()

        // Then
        for (i in 0 until viewModel.slots.size - 1) {
            assertTrue(
                "Slots should be in chronological order",
                viewModel.slots[i].slot.startDateTime.isBefore(viewModel.slots[i + 1].slot.startDateTime)
            )
        }
    }

    @Test
    fun `slots remain in chronological order after loading earlier dates`() {
        // Given
        viewModel = createViewModel()

        // When
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(0, 10))

        // Then
        for (i in 0 until viewModel.slots.size - 1) {
            assertTrue(
                "Slots should be in chronological order after loading earlier",
                viewModel.slots[i].slot.startDateTime.isBefore(viewModel.slots[i + 1].slot.startDateTime)
            )
        }
    }

    @Test
    fun `slots remain in chronological order after loading later dates`() {
        // Given
        viewModel = createViewModel()

        // When
        viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(viewModel.slots.size - 100, viewModel.slots.size - 1))

        // Then
        for (i in 0 until viewModel.slots.size - 1) {
            assertTrue(
                "Slots should be in chronological order after loading later",
                viewModel.slots[i].slot.startDateTime.isBefore(viewModel.slots[i + 1].slot.startDateTime)
            )
        }
    }

    // endregion
}
