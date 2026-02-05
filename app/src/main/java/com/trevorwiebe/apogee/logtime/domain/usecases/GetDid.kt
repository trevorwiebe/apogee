package com.trevorwiebe.apogee.logtime.domain.usecases

import com.trevorwiebe.apogee.logtime.data.Did
import com.trevorwiebe.apogee.logtime.data.DidDao
import kotlinx.coroutines.flow.Flow

class GetDid(
    private val didDao: DidDao
) {
    operator fun invoke(): Flow<List<Did>> {
        return didDao.getAllFlow()
    }
}
