package com.trevorwiebe.apogee.editschedulecould.presentation

import com.trevorwiebe.apogee.schedule.data.ScheduleCould

data class EditScheduleCouldState(
    val scheduleCouldList: List<ScheduleCould> = emptyList(),
    val editingScheduleCould: ScheduleCould? = null,
    val isDialogOpen: Boolean = false
)
