package com.trevorwiebe.apogee.schedule.presentation

import androidx.compose.ui.graphics.Color
import com.trevorwiebe.apogee.global.domain.FifteenMinSlot
import com.trevorwiebe.apogee.schedule.data.ScheduleCould

data class UiTimeSlot(
    val slot: FifteenMinSlot,
    val selected: Boolean,
    val scheduleCould: ScheduleCould?,
    val lightColor: Color?
)
