package com.trevorwiebe.apogee.global.domain.usecases

import com.trevorwiebe.apogee.global.domain.FifteenMinSlot
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalTime
import java.time.Duration

class CreateWeekFifteenIncrementsTest {

    @Test
    fun `invoke with default 1 week returns 672 slots`() {
        // Given
        val useCase = CreateWeekFifteenIncrements()

        // When
        val result = useCase()

        // Then
        assertEquals(672, result.size) // 96 slots/day * 7 days
    }

    @Test
    fun `invoke with 2 weeks returns 1344 slots`() {
        // Given
        val useCase = CreateWeekFifteenIncrements(numberOfWeeks = 2)

        // When
        val result = useCase()

        // Then
        assertEquals(1344, result.size) // 96 slots/day * 14 days
    }

    @Test
    fun `invoke with 4 weeks returns 2688 slots`() {
        // Given
        val useCase = CreateWeekFifteenIncrements(numberOfWeeks = 4)

        // When
        val result = useCase()

        // Then
        assertEquals(2688, result.size) // 96 slots/day * 28 days
    }

    @Test
    fun `invoke with 0 weeks returns empty list`() {
        // Given
        val useCase = CreateWeekFifteenIncrements(numberOfWeeks = 0)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `first slot of first day starts at midnight`() {
        // Given
        val useCase = CreateWeekFifteenIncrements()

        // When
        val result = useCase()
        val firstSlot = result.first()

        // Then
        assertEquals(LocalTime.of(0, 0, 0, 0), firstSlot.startTime)
        assertEquals(LocalTime.of(0, 14, 59, 999_999_999), firstSlot.endTime)
        assertEquals(1, firstSlot.dayOfTheWeek)
    }

    @Test
    fun `last slot of first day ends at end of day`() {
        // Given
        val useCase = CreateWeekFifteenIncrements()

        // When
        val result = useCase()
        val lastSlotOfDay = result[95] // 96th slot (0-indexed)

        // Then
        assertEquals(LocalTime.of(23, 45, 0, 0), lastSlotOfDay.startTime)
        assertEquals(LocalTime.of(23, 59, 59, 999_999_999), lastSlotOfDay.endTime)
        assertEquals(1, lastSlotOfDay.dayOfTheWeek)
    }

    @Test
    fun `all slots have exactly 15 minute duration minus 1 nanosecond`() {
        // Given
        val useCase = CreateWeekFifteenIncrements()

        // When
        val result = useCase()

        // Then
        result.forEach { slot ->
            val duration = Duration.between(slot.startTime, slot.endTime).plusNanos(1)
            assertEquals(
                "Slot ${slot.startTime} - ${slot.endTime} should be 15 minutes",
                Duration.ofMinutes(15),
                duration
            )
        }
    }

    @Test
    fun `no gaps between consecutive slots in same day`() {
        // Given
        val useCase = CreateWeekFifteenIncrements()

        // When
        val result = useCase()

        // Then
        for (i in 0 until result.size - 1) {
            val currentSlot = result[i]
            val nextSlot = result[i + 1]

            // Skip if moving to next day
            if (currentSlot.dayOfTheWeek != nextSlot.dayOfTheWeek) {
                continue
            }

            // Next slot should start exactly 1 nanosecond after current slot ends
            val expectedNextStart = currentSlot.endTime.plusNanos(1)
            assertEquals(
                "Gap found between slot ending at ${currentSlot.endTime} and starting at ${nextSlot.startTime}",
                expectedNextStart,
                nextSlot.startTime
            )
        }
    }

    @Test
    fun `no overlapping slots`() {
        // Given
        val useCase = CreateWeekFifteenIncrements()

        // When
        val result = useCase()

        // Then
        for (i in 0 until result.size - 1) {
            val currentSlot = result[i]
            val nextSlot = result[i + 1]

            // Skip if moving to next day
            if (currentSlot.dayOfTheWeek != nextSlot.dayOfTheWeek) {
                continue
            }

            // Current slot end time should be before next slot start time
            assertTrue(
                "Overlap found: ${currentSlot.endTime} >= ${nextSlot.startTime}",
                currentSlot.endTime.isBefore(nextSlot.startTime)
            )
        }
    }

    @Test
    fun `day numbering increments correctly across weeks`() {
        // Given
        val useCase = CreateWeekFifteenIncrements(numberOfWeeks = 3)

        // When
        val result = useCase()

        // Then
        // Week 1: days 1-7
        assertEquals(1, result[0].dayOfTheWeek) // First slot of day 1
        assertEquals(7, result[96 * 7 - 1].dayOfTheWeek) // Last slot of day 7

        // Week 2: days 8-14
        assertEquals(8, result[96 * 7].dayOfTheWeek) // First slot of day 8
        assertEquals(14, result[96 * 14 - 1].dayOfTheWeek) // Last slot of day 14

        // Week 3: days 15-21
        assertEquals(15, result[96 * 14].dayOfTheWeek) // First slot of day 15
        assertEquals(21, result[96 * 21 - 1].dayOfTheWeek) // Last slot of day 21
    }

    @Test
    fun `each day has exactly 96 slots`() {
        // Given
        val useCase = CreateWeekFifteenIncrements(numberOfWeeks = 2)

        // When
        val result = useCase()

        // Then
        val slotsByDay = result.groupBy { it.dayOfTheWeek }
        slotsByDay.forEach { (day, slots) ->
            assertEquals(
                "Day $day should have 96 slots",
                96,
                slots.size
            )
        }
    }

    @Test
    fun `slots are in chronological order within each day`() {
        // Given
        val useCase = CreateWeekFifteenIncrements()

        // When
        val result = useCase()

        // Then
        val slotsByDay = result.groupBy { it.dayOfTheWeek }
        slotsByDay.forEach { (day, slots) ->
            for (i in 0 until slots.size - 1) {
                assertTrue(
                    "Day $day slots should be in order: ${slots[i].startTime} should be before ${slots[i + 1].startTime}",
                    slots[i].startTime.isBefore(slots[i + 1].startTime)
                )
            }
        }
    }

    @Test
    fun `specific time slots are correct`() {
        // Given
        val useCase = CreateWeekFifteenIncrements()

        // When
        val result = useCase()

        // Then - Check some specific slots
        // Slot at 9:00 AM (slot index 36: 9*4)
        val slot9AM = result.find { it.startTime == LocalTime.of(9, 0) && it.dayOfTheWeek == 1 }
        assertNotNull(slot9AM)
        assertEquals(LocalTime.of(9, 0, 0, 0), slot9AM?.startTime)
        assertEquals(LocalTime.of(9, 14, 59, 999_999_999), slot9AM?.endTime)

        // Slot at 12:30 PM (slot index 50: 12*4 + 2)
        val slot1230PM = result.find { it.startTime == LocalTime.of(12, 30) && it.dayOfTheWeek == 1 }
        assertNotNull(slot1230PM)
        assertEquals(LocalTime.of(12, 30, 0, 0), slot1230PM?.startTime)
        assertEquals(LocalTime.of(12, 44, 59, 999_999_999), slot1230PM?.endTime)

        // Slot at 6:45 PM (slot index 75: 18*4 + 3)
        val slot645PM = result.find { it.startTime == LocalTime.of(18, 45) && it.dayOfTheWeek == 1 }
        assertNotNull(slot645PM)
        assertEquals(LocalTime.of(18, 45, 0, 0), slot645PM?.startTime)
        assertEquals(LocalTime.of(18, 59, 59, 999_999_999), slot645PM?.endTime)
    }

    @Test
    fun `all possible 15 minute increments are covered in a day`() {
        // Given
        val useCase = CreateWeekFifteenIncrements()

        // When
        val result = useCase()
        val day1Slots = result.filter { it.dayOfTheWeek == 1 }

        // Then - Check that we have slots starting at every 15-minute mark
        val expectedStartTimes = mutableListOf<LocalTime>()
        for (hour in 0..23) {
            for (minute in listOf(0, 15, 30, 45)) {
                expectedStartTimes.add(LocalTime.of(hour, minute))
            }
        }

        val actualStartTimes = day1Slots.map { it.startTime }
        assertEquals(expectedStartTimes, actualStartTimes)
    }

    @Test
    fun `midnight transition is handled correctly`() {
        // Given
        val useCase = CreateWeekFifteenIncrements()

        // When
        val result = useCase()

        // Then
        // Last slot of day 1
        val lastSlotDay1 = result.filter { it.dayOfTheWeek == 1 }.last()
        assertEquals(LocalTime.of(23, 45), lastSlotDay1.startTime)
        assertEquals(LocalTime.of(23, 59, 59, 999_999_999), lastSlotDay1.endTime)

        // First slot of day 2
        val firstSlotDay2 = result.filter { it.dayOfTheWeek == 2 }.first()
        assertEquals(LocalTime.of(0, 0), firstSlotDay2.startTime)
        assertEquals(LocalTime.of(0, 14, 59, 999_999_999), firstSlotDay2.endTime)
    }
}
