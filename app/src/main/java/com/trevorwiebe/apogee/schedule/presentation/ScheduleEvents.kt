package com.trevorwiebe.apogee.schedule.presentation

sealed class ScheduleEvents{
    data class OnSaveEvent(val title: String): ScheduleEvents()
}