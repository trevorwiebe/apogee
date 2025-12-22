package com.trevorwiebe.apogee.schedule.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {

    val timeSlots = viewModel.timeSlots

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(timeSlots){
            Text(text = it.startTime.toString())
        }
    }
}