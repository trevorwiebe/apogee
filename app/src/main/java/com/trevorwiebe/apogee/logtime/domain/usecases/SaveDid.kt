package com.trevorwiebe.apogee.logtime.domain.usecases

import com.trevorwiebe.apogee.logtime.data.Did
import com.trevorwiebe.apogee.logtime.data.DidDao

class SaveDid(
    private val didDao: DidDao
) {
    suspend operator fun invoke(did: Did) {
        didDao.insert(did)
    }
}
