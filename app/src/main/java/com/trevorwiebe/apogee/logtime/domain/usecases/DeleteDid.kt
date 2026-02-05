package com.trevorwiebe.apogee.logtime.domain.usecases

import com.trevorwiebe.apogee.logtime.data.DidDao
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DeleteDid(
    private val didDao: DidDao
) {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    suspend operator fun invoke(startDateTime: LocalDateTime) {
        didDao.deleteByStartDateTime(startDateTime.format(formatter))
    }
}
