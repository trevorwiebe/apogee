package com.trevorwiebe.apogee.schedule.domain.usecases

import com.trevorwiebe.apogee.schedule.data.ScheduleItem
import com.trevorwiebe.apogee.schedule.data.ScheduleItemDao
import kotlinx.coroutines.flow.Flow

class GetScheduledItems(
    private val scheduleItemDao: ScheduleItemDao
) {
    operator fun invoke(): Flow<List<ScheduleItem>> {
        return scheduleItemDao.getAllFlow()
    }
}