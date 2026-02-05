package com.trevorwiebe.apogee.logtime.presentation

sealed class LogTimeEvents {
    data class OnScrollPositionChanged(
        val firstVisibleIndex: Int,
        val lastVisibleIndex: Int
    ) : LogTimeEvents()
    data object OnScrollToNowClicked : LogTimeEvents()
    data class OnClick(val slot: LogTimeUiSlot) : LogTimeEvents()
    data class OnSaveActual(val scheduleCouldId: Int) : LogTimeEvents()
    data object OnDeselectAll : LogTimeEvents()
}
