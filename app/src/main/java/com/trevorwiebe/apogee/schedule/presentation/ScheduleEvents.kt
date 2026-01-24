package com.trevorwiebe.apogee.schedule.presentation

import androidx.compose.ui.graphics.Color

sealed class ScheduleEvents{
    data class OnClick(val timeSlot: UiTimeSlot): ScheduleEvents()
    data class OnLongClick(val timeSlot: UiTimeSlot): ScheduleEvents()
    data class OnSaveEvent(val title: String): ScheduleEvents()
    data class OnSaveScheduleCould(
        val title: String,
        val description: String,
        val color: Color
    ): ScheduleEvents()
}