package com.trevorwiebe.apogee.schedule.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trevorwiebe.apogee.schedule.presentation.UiTimeSlot

@Composable
fun ScheduleListItem(
    timeSlot: UiTimeSlot,
    onClick: (timeSlot: UiTimeSlot) -> Unit,
    onLongClick: (timeSlot: UiTimeSlot) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick(timeSlot) },
                onLongClick = { onLongClick(timeSlot) }
            )
            .padding(
                horizontal = 4.dp,
                vertical = 2.dp
            ),
        colors = CardDefaults.cardColors(
            containerColor = if(timeSlot.selected){
                MaterialTheme.colorScheme.tertiaryContainer
            }else{
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = timeSlot.slot.startTime.toString())
            Text(text = timeSlot.slot.endTime.plusNanos(1).toString())
        }
    }

}