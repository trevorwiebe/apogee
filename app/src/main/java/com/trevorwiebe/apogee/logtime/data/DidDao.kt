package com.trevorwiebe.apogee.logtime.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(did: Did)

    @Update
    suspend fun update(did: Did)

    @Delete
    suspend fun delete(did: Did)

    @Query("SELECT * FROM schedule_did WHERE id = :id")
    suspend fun getById(id: Int): Did?

    @Query("SELECT * FROM schedule_did WHERE startDateTime = :startDateTime")
    suspend fun getByStartDateTime(startDateTime: String): Did?

    @Query("SELECT * FROM schedule_did ORDER BY startDateTime ASC")
    fun getAllFlow(): Flow<List<Did>>

    @Query("DELETE FROM schedule_did WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM schedule_did WHERE startDateTime = :startDateTime")
    suspend fun deleteByStartDateTime(startDateTime: String)
}
