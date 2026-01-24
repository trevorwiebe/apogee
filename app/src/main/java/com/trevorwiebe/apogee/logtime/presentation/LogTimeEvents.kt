package com.trevorwiebe.apogee.logtime.presentation

sealed class LogTimeEvents {
    data class OnScrollPositionChanged(
        val firstVisibleIndex: Int,
        val lastVisibleIndex: Int
    ) : LogTimeEvents()
    data object OnScrollToNowClicked : LogTimeEvents()
}
