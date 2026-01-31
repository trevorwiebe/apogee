package com.trevorwiebe.apogee.testutils

import androidx.compose.ui.graphics.Color
import com.trevorwiebe.apogee.schedule.data.ScheduleCould
import com.trevorwiebe.apogee.schedule.data.ScheduleShould
import java.time.LocalTime

/**
 * Factory functions for creating test data with sensible defaults.
 * Override only the parameters you care about in each test.
 *
 * By default, ScheduleCould.id and ScheduleShould.scheduleCouldId both
 * default to 1, so they form a matching pair without extra configuration.
 */
object TestDataFactory {

    fun createScheduleCould(
        id: Int = 1,
        name: String = "Test Task",
        description: String = "Test description",
        color: Color = Color.Blue
    ) = ScheduleCould(
        id = id,
        name = name,
        description = description,
        color = color
    )

    fun createScheduleShould(
        id: Int = 1,
        scheduleCouldId: Int = 1,
        startTime: LocalTime = LocalTime.of(9, 0),
        endTime: LocalTime = LocalTime.of(9, 14, 59, 999_999_999),
        dayOfWeek: Int = 0
    ) = ScheduleShould(
        id = id,
        scheduleCouldId = scheduleCouldId,
        startTime = startTime,
        endTime = endTime,
        dayOfWeek = dayOfWeek
    )
}
