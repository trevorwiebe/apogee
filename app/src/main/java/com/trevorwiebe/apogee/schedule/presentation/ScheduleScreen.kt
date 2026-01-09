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
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.trevorwiebe.apogee.schedule.presentation.components.ScheduleBottomSheet
import com.trevorwiebe.apogee.schedule.presentation.components.ScheduleListItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {

    val timeSlots = viewModel.timeSlots
    val dayList = viewModel.dayList
    val systemBar = WindowInsets.navigationBars.asPaddingValues()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    val groupedByDay = timeSlots.groupBy { it.slot.dayOfTheWeek }

    LaunchedEffect(viewModel.anySelected) {
        scope.launch {
            if(viewModel.anySelected)
                scaffoldState.bottomSheetState.expand()
            else
                scaffoldState.bottomSheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            ScheduleBottomSheet(
                onSave = {
                    scope.launch {
                        scaffoldState.bottomSheetState.partialExpand()
                    }
                }
            )
        },
        sheetPeekHeight = 0.dp
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .displayCutoutPadding(),
            contentPadding = systemBar
        ) {
            groupedByDay.forEach { (dayIndex, slotsForDay) ->
                stickyHeader {
                    Text(
                        text = dayList[dayIndex],
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                items(slotsForDay) { slot ->
                    ScheduleListItem(
                        timeSlot = slot,
                        onClick = {
                            viewModel.onTimeSlotClicked(it)
                        },
                        onLongClick = {
                            viewModel.onTimeSlotLongClicked(it)
                        }
                    )
                }
            }
        }
    }
}