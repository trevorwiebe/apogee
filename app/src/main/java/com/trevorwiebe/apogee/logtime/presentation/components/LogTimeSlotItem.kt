package com.trevorwiebe.apogee.logtime.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trevorwiebe.apogee.logtime.presentation.LogTimeUiSlot

@Composable
fun LogTimeSlotItem(uiSlot: LogTimeUiSlot) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = if (uiSlot.scheduledName == null) 16.dp else 4.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = uiSlot.slot.startTime.toString())
            Text(text = uiSlot.slot.endTime.plusNanos(1).toString())
        }

        if (uiSlot.scheduledName == null) {
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(start = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = uiSlot.scheduledName,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
