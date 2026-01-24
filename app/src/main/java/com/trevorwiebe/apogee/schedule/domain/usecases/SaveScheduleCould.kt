package com.trevorwiebe.apogee.schedule.domain.usecases

import com.trevorwiebe.apogee.schedule.data.ScheduleCould
import com.trevorwiebe.apogee.schedule.data.ScheduleCouldDao

class SaveScheduleCould(
    private val scheduleCouldDao: ScheduleCouldDao
) {

    suspend operator fun invoke(scheduleCould: ScheduleCould) {
        scheduleCouldDao.insert(scheduleCould)
    }
}
