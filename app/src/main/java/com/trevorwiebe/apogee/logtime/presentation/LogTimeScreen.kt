package com.trevorwiebe.apogee.logtime.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.trevorwiebe.apogee.R
import com.trevorwiebe.apogee.logtime.presentation.components.DateHeader
import com.trevorwiebe.apogee.logtime.presentation.components.LogTimeBottomSheet
import com.trevorwiebe.apogee.logtime.presentation.components.LogTimeSlotItem
import com.trevorwiebe.apogee.schedule.presentation.components.customSheetHeight
import com.trevorwiebe.apogee.ui.Utils.displayCutoutPadding
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LogTimeScreen(
    onNavigate: (String) -> Unit,
    viewModel: LogTimeViewModel = hiltViewModel()
) {
    val slots = viewModel.slots
    val state = viewModel.state
    val scheduleCouldList = viewModel.scheduleCouldList
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val groupedByDate = slots.groupBy { it.slot.startDateTime.toLocalDate() }

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

    var bottomSheetHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(state.initialScrollIndex) {
        if (slots.isNotEmpty()) {
            listState.scrollToItem(state.initialScrollIndex)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            Pair(
                listState.firstVisibleItemIndex,
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            )
        }.collect { (firstVisible, lastVisible) ->
            viewModel.onEvent(LogTimeEvents.OnScrollPositionChanged(firstVisible, lastVisible))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.scrollToNowEvent.collect { index ->
            listState.animateScrollToItem(index)
        }
    }

    LaunchedEffect(state.anySelected) {
        scope.launch {
            if (state.anySelected) {
                scaffoldState.bottomSheetState.expand()
            } else {
                allowDismiss = true
                scaffoldState.bottomSheetState.hide()
                allowDismiss = false
            }
        }
    }

    BoxWithConstraints {

        bottomSheetHeight = customSheetHeight(constraints.maxHeight, scaffoldState)
        val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
        val displayCutout = displayCutoutPadding(LocalContext.current)

        BottomSheetScaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            scaffoldState = scaffoldState,
            sheetContent = {
                LogTimeBottomSheet(
                    scheduleCoulds = scheduleCouldList,
                    onItemClick = { scheduleCould ->
                        viewModel.onEvent(LogTimeEvents.OnSaveActual(scheduleCould.id))
                        scope.launch {
                            allowDismiss = true
                            scaffoldState.bottomSheetState.hide()
                            allowDismiss = false
                        }
                    },
                    onClose = {
                        viewModel.onEvent(LogTimeEvents.OnDeselectAll)
                        scope.launch {
                            allowDismiss = true
                            scaffoldState.bottomSheetState.hide()
                            allowDismiss = false
                        }
                    }
                )
            },
            sheetPeekHeight = 0.dp,
            sheetDragHandle = {}
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .padding(systemBarsPadding)
                    .padding(displayCutout)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopEnd
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    groupedByDate.forEach { (date, slotsForDate) ->
                        stickyHeader(key = date.toString()) {
                            DateHeader(date = date)
                        }
                        items(
                            items = slotsForDate,
                            key = { it.slot.startDateTime.toString() }
                        ) { uiSlot ->
                            LogTimeSlotItem(
                                uiSlot = uiSlot,
                                onClick = { viewModel.onEvent(LogTimeEvents.OnClick(it)) },
                            )
                        }
                    }
                }

                val buttonOffset by animateDpAsState(
                    targetValue = if (state.showScrollToNowButton) 0.dp else 64.dp,
                    label = "scrollToNowButtonOffset"
                )
                val tonalIconColor by animateColorAsState(
                    targetValue = if (state.showScrollToNowButton)
                        MaterialTheme.colorScheme.onSecondaryContainer
                    else MaterialTheme.colorScheme.surface,
                    label = "scrollToNowButtonColor"
                )
                val tonalButtonColor by animateColorAsState(
                    targetValue = if (state.showScrollToNowButton)
                        MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.surface,
                    label = "scrollToNowButtonColor"
                )

                Row(
                    modifier = Modifier.offset(x = buttonOffset),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { onNavigate("schedule") },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.calendar_edit),
                            contentDescription = null
                        )
                    }
                    FilledTonalIconButton(
                        onClick = { viewModel.onEvent(LogTimeEvents.OnScrollToNowClicked) },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(48.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = tonalButtonColor
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.calendar_today),
                            contentDescription = null,
                            tint = tonalIconColor
                        )
                    }
                }
            }
        }
    }
}
