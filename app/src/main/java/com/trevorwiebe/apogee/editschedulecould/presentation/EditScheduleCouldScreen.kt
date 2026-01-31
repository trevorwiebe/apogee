package com.trevorwiebe.apogee.editschedulecould.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.trevorwiebe.apogee.R
import com.trevorwiebe.apogee.editschedulecould.presentation.components.ScheduleCouldListItem
import com.trevorwiebe.apogee.global.presentation.ScheduleCouldDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScheduleCouldScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditScheduleCouldViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Activities") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(state.scheduleCouldList) { scheduleCould ->
                ScheduleCouldListItem(
                    scheduleCould = scheduleCould,
                    onEditClick = {
                        viewModel.onEvent(EditScheduleCouldEvents.OnEditClick(scheduleCould))
                    },
                    onDeleteClick = {
                        viewModel.onEvent(EditScheduleCouldEvents.OnDeleteClick(scheduleCould))
                    }
                )
            }
        }
    }

    if (state.isDialogOpen && state.editingScheduleCould != null) {
        ScheduleCouldDialog(
            scheduleCould = state.editingScheduleCould,
            onDismiss = {
                viewModel.onEvent(EditScheduleCouldEvents.OnDismissDialog)
            },
            onSave = { id, name, description, color ->
                viewModel.onEvent(EditScheduleCouldEvents.OnSaveEdit(id!!, name, description, color))
            }
        )
    }
}
