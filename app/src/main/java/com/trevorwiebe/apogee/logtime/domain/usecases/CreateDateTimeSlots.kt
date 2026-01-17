package com.trevorwiebe.apogee.logtime.domain.usecases

import com.trevorwiebe.apogee.logtime.domain.DateTimeSlot
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CreateDateTimeSlots {

    /**
     * Generate all 15-minute slots for a specific date
     */
    operator fun invoke(date: LocalDate): List<DateTimeSlot> {
        return (0 until 96).map { slotIndex ->
            val startMinutes = slotIndex * 15
            val startTime = LocalTime.of(startMinutes / 60, startMinutes % 60)
            val endTime = startTime.plusMinutes(15).minusNanos(1)

            DateTimeSlot(
                startDateTime = LocalDateTime.of(date, startTime),
                endDateTime = LocalDateTime.of(date, endTime)
            )
        }
    }

    /**
     * Generate slots for a date range (inclusive)
     */
    fun forRange(startDate: LocalDate, endDate: LocalDate): List<DateTimeSlot> {
        val slots = mutableListOf<DateTimeSlot>()
        var current = startDate
        while (!current.isAfter(endDate)) {
            slots.addAll(invoke(current))
            current = current.plusDays(1)
        }
        return slots
    }

    /**
     * Calculate the slot index for a given LocalDateTime within a day
     */
    fun slotIndexForTime(dateTime: LocalDateTime): Int {
        val hour = dateTime.hour
        val minute = dateTime.minute
        return (hour * 4) + (minute / 15)
    }
}
