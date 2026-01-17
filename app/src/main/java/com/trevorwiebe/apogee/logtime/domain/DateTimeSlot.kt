package com.trevorwiebe.apogee.logtime.domain

import java.time.LocalDateTime
import java.time.LocalTime

data class DateTimeSlot(
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime
) {
    // Converts to 0-indexed day (0=Monday, 6=Sunday) for ScheduleShould matching
    val zeroDayOfWeek: Int
        get() = startDateTime.dayOfWeek.value - 1

    val startTime: LocalTime
        get() = startDateTime.toLocalTime()

    val endTime: LocalTime
        get() = endDateTime.toLocalTime()
}
