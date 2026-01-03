package com.trevorwiebe.apogee.schedule.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.trevorwiebe.apogee.schedule.presentation.components.ScheduleListItem

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {

    val timeSlots = viewModel.timeSlots
    val dayList = viewModel.dayList
    val systemBar = WindowInsets.navigationBars.asPaddingValues()

    val groupedByDay = timeSlots.groupBy { it.dayOfTheWeek }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .displayCutoutPadding(),
        contentPadding = systemBar
    ) {
        groupedByDay.forEach { (dayIndex, slotsForDay) ->
            stickyHeader {
                Text(
                    text = dayList[dayIndex],
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            items(slotsForDay) { slot ->
                ScheduleListItem(slot)
            }
        }
    }
}