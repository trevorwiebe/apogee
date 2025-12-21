package com.trevorwiebe.apogee.global.domain

import java.time.LocalTime

data class FifteenMinSlot(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val dayOfTheWeek: Int
)
