package com.trevorwiebe.apogee.schedule.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.trevorwiebe.apogee.schedule.presentation.components.ScheduleListItem

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {

    val timeSlots = viewModel.timeSlots
    val safeDrawing = WindowInsets.safeDrawing.asPaddingValues()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = safeDrawing
    ) {
        items(timeSlots){
            ScheduleListItem(it)
        }
    }
}