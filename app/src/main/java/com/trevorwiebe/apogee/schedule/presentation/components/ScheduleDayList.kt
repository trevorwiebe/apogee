package com.trevorwiebe.apogee.schedule.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.trevorwiebe.apogee.schedule.presentation.UiTimeSlot

@Composable
fun ScheduleDayList(
    list: List<UiTimeSlot>,
    bottomSheetHeight: Dp,
    onClick: (UiTimeSlot) -> Unit,
    onLongClick: (UiTimeSlot) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = bottomSheetHeight)
    ) {
        items(list) { slot ->
            ScheduleListItem(
                timeSlot = slot,
                onClick = onClick,
                onLongClick = onLongClick
            )
        }
    }

}