package com.trevorwiebe.apogee.schedule.presentation

import com.trevorwiebe.apogee.schedule.data.ScheduleItem

object ScheduleVMUtils {

    internal fun mapTaskToTime(
        timeList: List<UiTimeSlot>,
        taskList: List<ScheduleItem>
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