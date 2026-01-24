package com.trevorwiebe.apogee.schedule.domain.usecases

import com.trevorwiebe.apogee.schedule.data.ScheduleCould
import com.trevorwiebe.apogee.schedule.data.ScheduleCouldDao
import kotlinx.coroutines.flow.Flow

class GetScheduleCould(
    private val scheduleCouldDao: ScheduleCouldDao
) {
    operator fun invoke(): Flow<List<ScheduleCould>> {
        return scheduleCouldDao.getAllFlow()
    }
}
