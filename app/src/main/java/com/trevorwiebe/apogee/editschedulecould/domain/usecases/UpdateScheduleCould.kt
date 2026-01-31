package com.trevorwiebe.apogee.editschedulecould.domain.usecases

import com.trevorwiebe.apogee.schedule.data.ScheduleCould
import com.trevorwiebe.apogee.schedule.data.ScheduleCouldDao

class UpdateScheduleCould(
    private val scheduleCouldDao: ScheduleCouldDao
) {
    suspend operator fun invoke(scheduleCould: ScheduleCould) {
        scheduleCouldDao.update(scheduleCould)
    }
}
