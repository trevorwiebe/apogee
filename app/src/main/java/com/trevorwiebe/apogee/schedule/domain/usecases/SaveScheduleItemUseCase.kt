package com.trevorwiebe.apogee.schedule.domain.usecases

import com.trevorwiebe.apogee.schedule.data.ScheduleItem
import com.trevorwiebe.apogee.schedule.data.ScheduleItemDao

class SaveScheduleItemUseCase(
    private val scheduleItemDao: ScheduleItemDao
) {

    suspend operator fun invoke(scheduleItem: ScheduleItem) {
        scheduleItemDao.insert(scheduleItem)
    }
}
