package com.trevorwiebe.apogee.schedule.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.ui.draw.clip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
                        top = if(timeSlot.scheduleCould == null) 10.dp else 6.dp,
                        bottom = if(timeSlot.scheduleCould == null) 10.dp else 6.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = timeSlot.slot.startTime.toString())
                val default = MaterialTheme.colorScheme.surfaceVariant
                if(timeSlot.scheduleCould != null){
                    Text(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .weight(1f)
                            .background(timeSlot.lightColor ?: default)
                            .padding(start = 16.dp, end = 16.dp),
                        textAlign = TextAlign.Center,
                        text = timeSlot.scheduleCould.name,
                        fontSize = 14.sp,
                        color = timeSlot.scheduleCould.color,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }else{
                    Spacer(modifier = Modifier.weight(1f))
                }
                Text(text = timeSlot.slot.endTime.plusNanos(1).toString())
            }
        }
    }

}