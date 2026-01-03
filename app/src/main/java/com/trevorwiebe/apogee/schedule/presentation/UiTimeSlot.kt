package com.trevorwiebe.apogee.schedule.presentation

import com.trevorwiebe.apogee.global.domain.FifteenMinSlot

data class UiTimeSlot(
    val slot: FifteenMinSlot,
    val selected: Boolean,
)
