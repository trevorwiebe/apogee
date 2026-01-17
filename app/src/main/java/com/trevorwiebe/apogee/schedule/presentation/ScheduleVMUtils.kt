package com.trevorwiebe.apogee.schedule.presentation

import com.trevorwiebe.apogee.schedule.data.ScheduleShould

object ScheduleVMUtils {

    internal fun mapTaskToTime(
        timeList: List<UiTimeSlot>,
        taskList: List<ScheduleShould>
    ): List<UiTimeSlot>{
        return timeList.map { time ->
            val task = taskList.find {
                it.startTime <= time.slot.startTime &&
                it.endTime >= time.slot.endTime &&
                it.dayOfWeek == time.slot.dayOfTheWeek
            }
            time.copy(task = task)
        }
    }
}