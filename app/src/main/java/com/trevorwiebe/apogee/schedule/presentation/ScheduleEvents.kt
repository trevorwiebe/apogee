package com.trevorwiebe.apogee.schedule.presentation

sealed class ScheduleEvents{
    data class OnClick(val timeSlot: UiTimeSlot): ScheduleEvents()
    data class OnLongClick(val timeSlot: UiTimeSlot): ScheduleEvents()
    data class OnSaveEvent(val title: String): ScheduleEvents()
}