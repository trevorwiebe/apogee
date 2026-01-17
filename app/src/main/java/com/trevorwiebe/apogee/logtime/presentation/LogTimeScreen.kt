package com.trevorwiebe.apogee.logtime.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.trevorwiebe.apogee.R
import com.trevorwiebe.apogee.logtime.presentation.components.DateHeader
import com.trevorwiebe.apogee.logtime.presentation.components.LogTimeSlotItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogTimeScreen(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit,
    viewModel: LogTimeViewModel = hiltViewModel()
) {
    val slots = viewModel.slots
    val state = viewModel.state
    val listState = rememberLazyListState()

    val groupedByDate = slots.groupBy { it.slot.startDateTime.toLocalDate() }

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

    Box(
        modifier = modifier
            .systemBarsPadding()
            .fillMaxSize()
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
                    LogTimeSlotItem(uiSlot = uiSlot)
                }
            }
        }

        FilledTonalIconButton(
            onClick = { onNavigate("schedule") },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .displayCutoutPadding()
                .padding(16.dp)
                .size(54.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.edit_calendar),
                contentDescription = null
            )
        }
    }
}
