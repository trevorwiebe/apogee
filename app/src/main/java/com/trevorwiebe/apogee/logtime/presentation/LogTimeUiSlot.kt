package com.trevorwiebe.apogee.logtime.presentation

import androidx.compose.ui.graphics.Color
import com.trevorwiebe.apogee.logtime.domain.DateTimeSlot

data class LogTimeUiSlot(
    val slot: DateTimeSlot,
    val scheduledName: String? = null,
    val color: Color? = null,
    val lightColor: Color? = null
)
