package com.trevorwiebe.apogee.logtime.presentation

import com.trevorwiebe.apogee.logtime.domain.DateTimeSlot
import com.trevorwiebe.apogee.schedule.data.ScheduleShould

data class LogTimeUiSlot(
    val slot: DateTimeSlot,
    val scheduledName: String? = null
)
