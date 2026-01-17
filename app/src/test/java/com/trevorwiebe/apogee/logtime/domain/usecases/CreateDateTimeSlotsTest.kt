package com.trevorwiebe.apogee.logtime.domain.usecases

import org.junit.Assert.*
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CreateDateTimeSlotsTest {

    @Test
    fun `invoke returns 96 slots for a single date`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val date = LocalDate.of(2025, 1, 15)

        // When
        val result = useCase(date)

        // Then
        assertEquals(96, result.size)
    }

    @Test
    fun `first slot of day starts at midnight`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val date = LocalDate.of(2025, 1, 15)

        // When
        val result = useCase(date)
        val firstSlot = result.first()

        // Then
        assertEquals(LocalDateTime.of(date, LocalTime.of(0, 0, 0, 0)), firstSlot.startDateTime)
        assertEquals(LocalDateTime.of(date, LocalTime.of(0, 14, 59, 999_999_999)), firstSlot.endDateTime)
    }

    @Test
    fun `last slot of day ends at end of day`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val date = LocalDate.of(2025, 1, 15)

        // When
        val result = useCase(date)
        val lastSlot = result.last()

        // Then
        assertEquals(LocalDateTime.of(date, LocalTime.of(23, 45, 0, 0)), lastSlot.startDateTime)
        assertEquals(LocalDateTime.of(date, LocalTime.of(23, 59, 59, 999_999_999)), lastSlot.endDateTime)
    }

    @Test
    fun `all slots have exactly 15 minute duration minus 1 nanosecond`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val date = LocalDate.of(2025, 1, 15)

        // When
        val result = useCase(date)

        // Then
        result.forEach { slot ->
            val duration = Duration.between(slot.startDateTime, slot.endDateTime).plusNanos(1)
            assertEquals(
                "Slot ${slot.startDateTime} - ${slot.endDateTime} should be 15 minutes",
                Duration.ofMinutes(15),
                duration
            )
        }
    }

    @Test
    fun `no gaps between consecutive slots`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val date = LocalDate.of(2025, 1, 15)

        // When
        val result = useCase(date)

        // Then
        for (i in 0 until result.size - 1) {
            val currentSlot = result[i]
            val nextSlot = result[i + 1]

            val expectedNextStart = currentSlot.endDateTime.plusNanos(1)
            assertEquals(
                "Gap found between slot ending at ${currentSlot.endDateTime} and starting at ${nextSlot.startDateTime}",
                expectedNextStart,
                nextSlot.startDateTime
            )
        }
    }

    @Test
    fun `no overlapping slots`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val date = LocalDate.of(2025, 1, 15)

        // When
        val result = useCase(date)

        // Then
        for (i in 0 until result.size - 1) {
            val currentSlot = result[i]
            val nextSlot = result[i + 1]

            assertTrue(
                "Overlap found: ${currentSlot.endDateTime} >= ${nextSlot.startDateTime}",
                currentSlot.endDateTime.isBefore(nextSlot.startDateTime)
            )
        }
    }

    @Test
    fun `slots are in chronological order`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val date = LocalDate.of(2025, 1, 15)

        // When
        val result = useCase(date)

        // Then
        for (i in 0 until result.size - 1) {
            assertTrue(
                "Slots should be in order: ${result[i].startDateTime} should be before ${result[i + 1].startDateTime}",
                result[i].startDateTime.isBefore(result[i + 1].startDateTime)
            )
        }
    }

    @Test
    fun `all possible 15 minute increments are covered`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val date = LocalDate.of(2025, 1, 15)

        // When
        val result = useCase(date)

        // Then
        val expectedStartTimes = mutableListOf<LocalTime>()
        for (hour in 0..23) {
            for (minute in listOf(0, 15, 30, 45)) {
                expectedStartTimes.add(LocalTime.of(hour, minute))
            }
        }

        val actualStartTimes = result.map { it.startTime }
        assertEquals(expectedStartTimes, actualStartTimes)
    }

    @Test
    fun `specific time slots are correct`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val date = LocalDate.of(2025, 1, 15)

        // When
        val result = useCase(date)

        // Then
        val slot9AM = result.find { it.startTime == LocalTime.of(9, 0) }
        assertNotNull(slot9AM)
        assertEquals(LocalTime.of(9, 0, 0, 0), slot9AM?.startTime)
        assertEquals(LocalTime.of(9, 14, 59, 999_999_999), slot9AM?.endTime)

        val slot1230PM = result.find { it.startTime == LocalTime.of(12, 30) }
        assertNotNull(slot1230PM)
        assertEquals(LocalTime.of(12, 30, 0, 0), slot1230PM?.startTime)
        assertEquals(LocalTime.of(12, 44, 59, 999_999_999), slot1230PM?.endTime)
    }

    // region forRange tests

    @Test
    fun `forRange returns correct number of slots for single day`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val startDate = LocalDate.of(2025, 1, 15)
        val endDate = LocalDate.of(2025, 1, 15)

        // When
        val result = useCase.forRange(startDate, endDate)

        // Then
        assertEquals(96, result.size)
    }

    @Test
    fun `forRange returns correct number of slots for multiple days`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val startDate = LocalDate.of(2025, 1, 15)
        val endDate = LocalDate.of(2025, 1, 17)

        // When
        val result = useCase.forRange(startDate, endDate)

        // Then
        assertEquals(288, result.size) // 96 * 3 days
    }

    @Test
    fun `forRange returns correct number of slots for week`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val startDate = LocalDate.of(2025, 1, 13)
        val endDate = LocalDate.of(2025, 1, 19)

        // When
        val result = useCase.forRange(startDate, endDate)

        // Then
        assertEquals(672, result.size) // 96 * 7 days
    }

    @Test
    fun `forRange returns empty list when end date before start date`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val startDate = LocalDate.of(2025, 1, 17)
        val endDate = LocalDate.of(2025, 1, 15)

        // When
        val result = useCase.forRange(startDate, endDate)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `forRange slots are in chronological order across days`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val startDate = LocalDate.of(2025, 1, 15)
        val endDate = LocalDate.of(2025, 1, 17)

        // When
        val result = useCase.forRange(startDate, endDate)

        // Then
        for (i in 0 until result.size - 1) {
            assertTrue(
                "Slots should be in order across days",
                result[i].startDateTime.isBefore(result[i + 1].startDateTime)
            )
        }
    }

    @Test
    fun `forRange correctly transitions between days`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val startDate = LocalDate.of(2025, 1, 15)
        val endDate = LocalDate.of(2025, 1, 16)

        // When
        val result = useCase.forRange(startDate, endDate)

        // Then
        val lastSlotDay1 = result[95]
        val firstSlotDay2 = result[96]

        assertEquals(LocalDate.of(2025, 1, 15), lastSlotDay1.startDateTime.toLocalDate())
        assertEquals(LocalTime.of(23, 45), lastSlotDay1.startTime)

        assertEquals(LocalDate.of(2025, 1, 16), firstSlotDay2.startDateTime.toLocalDate())
        assertEquals(LocalTime.of(0, 0), firstSlotDay2.startTime)
    }

    // endregion

    // region slotIndexForTime tests

    @Test
    fun `slotIndexForTime returns 0 for midnight`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val dateTime = LocalDateTime.of(2025, 1, 15, 0, 0)

        // When
        val result = useCase.slotIndexForTime(dateTime)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `slotIndexForTime returns correct index for 9 AM`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val dateTime = LocalDateTime.of(2025, 1, 15, 9, 0)

        // When
        val result = useCase.slotIndexForTime(dateTime)

        // Then
        assertEquals(36, result) // 9 * 4
    }

    @Test
    fun `slotIndexForTime returns correct index for 12-30 PM`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val dateTime = LocalDateTime.of(2025, 1, 15, 12, 30)

        // When
        val result = useCase.slotIndexForTime(dateTime)

        // Then
        assertEquals(50, result) // 12 * 4 + 2
    }

    @Test
    fun `slotIndexForTime returns correct index for 23-45`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val dateTime = LocalDateTime.of(2025, 1, 15, 23, 45)

        // When
        val result = useCase.slotIndexForTime(dateTime)

        // Then
        assertEquals(95, result) // 23 * 4 + 3
    }

    @Test
    fun `slotIndexForTime rounds down for times within a slot`() {
        // Given
        val useCase = CreateDateTimeSlots()

        // When/Then
        assertEquals(0, useCase.slotIndexForTime(LocalDateTime.of(2025, 1, 15, 0, 7)))
        assertEquals(0, useCase.slotIndexForTime(LocalDateTime.of(2025, 1, 15, 0, 14)))
        assertEquals(1, useCase.slotIndexForTime(LocalDateTime.of(2025, 1, 15, 0, 15)))
        assertEquals(1, useCase.slotIndexForTime(LocalDateTime.of(2025, 1, 15, 0, 29)))
        assertEquals(2, useCase.slotIndexForTime(LocalDateTime.of(2025, 1, 15, 0, 30)))
    }

    // endregion

    // region zeroDayOfWeek tests

    @Test
    fun `zeroDayOfWeek returns 0 for Monday`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val monday = LocalDate.of(2025, 1, 13) // Monday

        // When
        val result = useCase(monday)

        // Then
        assertEquals(0, result.first().zeroDayOfWeek)
    }

    @Test
    fun `zeroDayOfWeek returns 6 for Sunday`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val sunday = LocalDate.of(2025, 1, 19) // Sunday

        // When
        val result = useCase(sunday)

        // Then
        assertEquals(6, result.first().zeroDayOfWeek)
    }

    @Test
    fun `zeroDayOfWeek returns correct values for all days`() {
        // Given
        val useCase = CreateDateTimeSlots()
        val monday = LocalDate.of(2025, 1, 13)

        // When/Then
        for (dayOffset in 0..6) {
            val date = monday.plusDays(dayOffset.toLong())
            val slots = useCase(date)
            assertEquals(dayOffset, slots.first().zeroDayOfWeek)
        }
    }

    // endregion
}
