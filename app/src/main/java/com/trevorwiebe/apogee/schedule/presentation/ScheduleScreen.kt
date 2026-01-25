package com.trevorwiebe.apogee.schedule.presentation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.SheetValue
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.trevorwiebe.apogee.R
import com.trevorwiebe.apogee.schedule.presentation.addScheduleCould.AddScheduleCouldDialog
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
    var allowDismiss by remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
            confirmValueChange = { targetValue ->
                if (targetValue == SheetValue.Hidden || targetValue == SheetValue.PartiallyExpanded) {
                    allowDismiss
                } else true
            }
        )
    )
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { groupedByDay.size },
    )

    var bottomSheetHeight by remember { mutableStateOf(0.dp) }
    val scheduleCouldDialogOpen = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel.anySelected) {
        scope.launch {
            if(viewModel.anySelected) {
                scaffoldState.bottomSheetState.expand()
            } else {
                allowDismiss = true
                scaffoldState.bottomSheetState.hide()
                allowDismiss = false
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow {pagerState.currentPage }.collect { position ->
            viewModel.weekDaySelected = position
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.snackbarEvent.collect { message ->
            scaffoldState.snackbarHostState.showSnackbar(message)
        }
    }

    BoxWithConstraints {

        bottomSheetHeight = customSheetHeight(constraints.maxHeight, scaffoldState)
        val systemBarsBottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

        BottomSheetScaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(
                    hostState = scaffoldState.snackbarHostState,
                    modifier = Modifier.padding(bottom = systemBarsBottom)
                ) { snackbarData ->
                    Snackbar(snackbarData = snackbarData)
                }
            },
            sheetContent = {
                ScheduleBottomSheet(
                    scheduleCoulds = viewModel.scheduleCoulds,
                    onItemClick = { scheduleCould ->
                        viewModel.onEvent(ScheduleEvents.OnSaveEvent(scheduleCould.name))
                        scope.launch {
                            allowDismiss = true
                            scaffoldState.bottomSheetState.hide()
                            allowDismiss = false
                        }
                    },
                    onClose = {
                        viewModel.onEvent(ScheduleEvents.OnDeselectAll)
                        scope.launch {
                            allowDismiss = true
                            scaffoldState.bottomSheetState.hide()
                            allowDismiss = false
                        }
                    }
                )
            },
            sheetPeekHeight = 0.dp,
            sheetDragHandle = {},
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Schedule")
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                scheduleCouldDialogOpen.value = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.calendar_add),
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(bottom = systemBarsBottom)
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
                        onClick = {
                            viewModel.onEvent(ScheduleEvents.OnClick(it))
                        },
                        onLongClick = {
                            viewModel.onEvent(ScheduleEvents.OnLongClick(it))
                        }
                    )
                }
            }
        }
    }

    AddScheduleCouldDialog(sheetOpen = scheduleCouldDialogOpen){
        title, description, color ->
        viewModel.onEvent(ScheduleEvents.OnSaveScheduleCould(title, description, color))
        scheduleCouldDialogOpen.value = false
    }
}