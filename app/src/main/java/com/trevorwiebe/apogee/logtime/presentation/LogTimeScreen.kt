package com.trevorwiebe.apogee.logtime.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trevorwiebe.apogee.R
import com.trevorwiebe.apogee.logtime.presentation.components.DateHeader
import com.trevorwiebe.apogee.logtime.presentation.components.LogTimeSlotItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogTimeScreen(
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

    LaunchedEffect(Unit) {
        viewModel.scrollToNowEvent.collect { index ->
            listState.animateScrollToItem(index)
        }
    }

    Scaffold{ padding ->
        Box(
            modifier = Modifier
                .padding(padding)
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
                        LogTimeSlotItem(uiSlot = uiSlot)
                    }
                }
            }

            val buttonOffset by animateDpAsState(
                targetValue = if (state.showScrollToNowButton) 0.dp else 64.dp,
                label = "scrollToNowButtonOffset"
            )
            Row(
                modifier = Modifier.offset(x = buttonOffset),
                verticalAlignment = Alignment.CenterVertically,
            ){
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
                        .size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.calendar_today),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
