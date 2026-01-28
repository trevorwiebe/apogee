package com.trevorwiebe.apogee.logtime.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            Text(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 2.dp),
                text = "Activity: ${uiSlot.scheduledName}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
