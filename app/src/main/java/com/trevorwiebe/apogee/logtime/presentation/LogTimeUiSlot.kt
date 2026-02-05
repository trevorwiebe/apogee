package com.trevorwiebe.apogee.logtime.presentation

import androidx.compose.ui.graphics.Color
import com.trevorwiebe.apogee.logtime.domain.DateTimeSlot

data class LogTimeUiSlot(
    val slot: DateTimeSlot,
    val scheduledName: String? = null,
    val color: Color? = null,
    val lightColor: Color? = null,
    val actualName: String? = null,
    val actualColor: Color? = null,
    val actualLightColor: Color? = null,
    val selected: Boolean = false
)
