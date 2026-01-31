package com.trevorwiebe.apogee.editschedulecould.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.apogee.editschedulecould.domain.usecases.DeleteScheduleCould
import com.trevorwiebe.apogee.editschedulecould.domain.usecases.UpdateScheduleCould
import com.trevorwiebe.apogee.schedule.data.ScheduleCould
import com.trevorwiebe.apogee.schedule.domain.usecases.GetScheduleCould
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditScheduleCouldViewModel @Inject constructor(
    private val getScheduleCould: GetScheduleCould,
    private val updateScheduleCould: UpdateScheduleCould,
    private val deleteScheduleCould: DeleteScheduleCould
) : ViewModel() {

    var state by mutableStateOf(EditScheduleCouldState())
        private set

    init {
        loadScheduleCoulds()
    }

    private fun loadScheduleCoulds() {
        viewModelScope.launch {
            getScheduleCould().collect { scheduleCouldList ->
                state = state.copy(scheduleCouldList = scheduleCouldList)
            }
        }
    }

    fun onEvent(event: EditScheduleCouldEvents) {
        when (event) {
            is EditScheduleCouldEvents.OnEditClick -> handleEditClick(event.scheduleCould)
            is EditScheduleCouldEvents.OnDeleteClick -> handleDeleteClick(event.scheduleCould)
            is EditScheduleCouldEvents.OnSaveEdit -> handleSaveEdit(event)
            is EditScheduleCouldEvents.OnDismissDialog -> handleDismissDialog()
        }
    }

    private fun handleEditClick(scheduleCould: ScheduleCould) {
        state = state.copy(
            editingScheduleCould = scheduleCould,
            isDialogOpen = true
        )
    }

    private fun handleDeleteClick(scheduleCould: ScheduleCould) {
        viewModelScope.launch {
            deleteScheduleCould(scheduleCould)
        }
    }

    private fun handleSaveEdit(event: EditScheduleCouldEvents.OnSaveEdit) {
        val updatedScheduleCould = ScheduleCould(
            id = event.id,
            name = event.name,
            description = event.description,
            color = event.color
        )
        viewModelScope.launch {
            updateScheduleCould(updatedScheduleCould)
            state = state.copy(
                editingScheduleCould = null,
                isDialogOpen = false
            )
        }
    }

    private fun handleDismissDialog() {
        state = state.copy(
            editingScheduleCould = null,
            isDialogOpen = false
        )
    }
}
