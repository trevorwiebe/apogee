package com.trevorwiebe.apogee.schedule.presentation.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.ui.draw.clip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(
                horizontal = 4.dp,
                vertical = 2.dp
            )
            .clip(MaterialTheme.shapes.small)
            .combinedClickable(
                onClick = { onClick(timeSlot) },
                onLongClick = { onLongClick(timeSlot) },
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
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = if(timeSlot.taskName == null)
                            16.dp else 4.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = timeSlot.slot.startTime.toString())
                Text(text = timeSlot.slot.endTime.plusNanos(1).toString())
            }
            if(timeSlot.taskName == null){
                Spacer(modifier = Modifier.height(16.dp))
            }else{
                Text(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 2.dp),
                    text = "Task: ${timeSlot.taskName}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }

}