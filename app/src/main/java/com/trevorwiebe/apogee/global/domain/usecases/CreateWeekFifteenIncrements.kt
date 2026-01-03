package com.trevorwiebe.apogee.global.domain.usecases

import com.trevorwiebe.apogee.global.domain.FifteenMinSlot
import java.time.LocalTime

class CreateWeekFifteenIncrements(
    private val numberOfWeeks: Int = 1
) {

    operator fun invoke(): List<FifteenMinSlot> {
        val slots = mutableListOf<FifteenMinSlot>()

        // Loop through each week
        for (week in 0 until numberOfWeeks) {
            for (dayOfWeek in 0..6) {
                val absoluteDayOfWeek = (week * 7) + dayOfWeek
                for (slotIndex in 0 until 96) {
                    val startMinutes = slotIndex * 15
                    val startTime = LocalTime.of(startMinutes / 60, startMinutes % 60)
                    // End time is 14:59.999999999 after start (last nanosecond before next slot)
                    val endTime = startTime.plusMinutes(15).minusNanos(1)

                    slots.add(
                        FifteenMinSlot(
                            startTime = startTime,
                            endTime = endTime,
                            dayOfTheWeek = absoluteDayOfWeek
                        )
                    )
                }
            }
        }

        return slots
    }
}