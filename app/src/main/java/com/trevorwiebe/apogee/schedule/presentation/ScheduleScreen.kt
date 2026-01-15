package com.trevorwiebe.apogee.schedule.presentation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.trevorwiebe.apogee.schedule.presentation.components.ScheduleBottomSheet
import com.trevorwiebe.apogee.schedule.presentation.components.ScheduleDayList
import com.trevorwiebe.apogee.schedule.presentation.components.Tabs
import com.trevorwiebe.apogee.schedule.presentation.components.customSheetHeight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {

    val timeSlots = viewModel.timeSlots

    val groupedByDay = timeSlots.groupBy { it.slot.dayOfTheWeek }

    val dayList = viewModel.dayList
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { groupedByDay.size },
    )

    var bottomSheetHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(viewModel.anySelected) {
        scope.launch {
            if(viewModel.anySelected)
                scaffoldState.bottomSheetState.expand()
            else
                scaffoldState.bottomSheetState.partialExpand()
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow {pagerState.currentPage }.collect { position ->
            viewModel.weekDaySelected = position
        }
    }

    BoxWithConstraints {

        bottomSheetHeight = customSheetHeight(constraints.maxHeight, scaffoldState)

        BottomSheetScaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            scaffoldState = scaffoldState,
            sheetContent = {
                ScheduleBottomSheet(
                    saveButtonEnabled = viewModel.saveButtonEnabled,
                    onSave = { title ->
                        viewModel.onEvent(ScheduleEvents.OnSaveEvent(title))
                        scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                    }
                )
            },
            sheetPeekHeight = 0.dp
        ) { padding ->

            Column(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxSize()
            ) {
                Tabs(
                    tabs = dayList,
                    selectedTabIndex = pagerState.currentPage,
                    onTabSelected = { index ->
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { position ->
                    ScheduleDayList(
                        list = groupedByDay[position] ?: emptyList(),
                        bottomSheetHeight = bottomSheetHeight,
                        onClick = { viewModel.onTimeSlotClicked(it) },
                        onLongClick = { viewModel.onTimeSlotLongClicked(it) }
                    )
                }
            }
        }
    }
}