package com.trevorwiebe.apogee.editschedulecould.presentation

import androidx.compose.ui.graphics.Color
import com.trevorwiebe.apogee.schedule.data.ScheduleCould

sealed class EditScheduleCouldEvents {
    data class OnEditClick(val scheduleCould: ScheduleCould) : EditScheduleCouldEvents()
    data class OnDeleteClick(val scheduleCould: ScheduleCould) : EditScheduleCouldEvents()
    data class OnSaveEdit(
        val id: Int,
        val name: String,
        val description: String,
        val color: Color
    ) : EditScheduleCouldEvents()
    data object OnDismissDialog : EditScheduleCouldEvents()
}
