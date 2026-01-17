package com.trevorwiebe.apogee.schedule.domain.usecases

import com.trevorwiebe.apogee.schedule.data.ScheduleShould
import com.trevorwiebe.apogee.schedule.data.ScheduleShouldDao
import kotlinx.coroutines.flow.Flow

class GetScheduledShould(
    private val scheduleShouldDao: ScheduleShouldDao
) {
    operator fun invoke(): Flow<List<ScheduleShould>> {
        return scheduleShouldDao.getAllFlow()
    }
}