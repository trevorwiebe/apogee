package com.trevorwiebe.apogee.schedule.domain.usecases

import com.trevorwiebe.apogee.schedule.data.ScheduleShould
import com.trevorwiebe.apogee.schedule.data.ScheduleShouldDao

class SaveScheduleShould(
    private val scheduleShouldDao: ScheduleShouldDao
) {

    suspend operator fun invoke(scheduleShould: ScheduleShould) {
        scheduleShouldDao.insert(scheduleShould)
    }

    suspend operator fun invoke(scheduleShouldList: List<ScheduleShould>) {
        scheduleShouldDao.insertAll(scheduleShouldList)
    }
}
