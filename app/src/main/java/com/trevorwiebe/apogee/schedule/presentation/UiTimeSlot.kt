package com.trevorwiebe.apogee.schedule.presentation

import com.trevorwiebe.apogee.global.domain.FifteenMinSlot
import com.trevorwiebe.apogee.schedule.data.ScheduleItem

data class UiTimeSlot(
    val slot: FifteenMinSlot,
    val selected: Boolean,
    val task: ScheduleItem?
)
