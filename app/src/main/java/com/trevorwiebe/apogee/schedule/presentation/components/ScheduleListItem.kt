package com.trevorwiebe.apogee.schedule.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trevorwiebe.apogee.global.domain.FifteenMinSlot

@Composable
fun ScheduleListItem(
    timeSlot: FifteenMinSlot
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp,
                vertical = 2.dp
            ),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(text = timeSlot.startTime.toString())
            Text(text = timeSlot.endTime.toString())
        }
    }

}