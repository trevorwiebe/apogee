package com.trevorwiebe.apogee.schedule.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scheduleDid: ScheduleDid)

    @Update
    suspend fun update(scheduleDid: ScheduleDid)

    @Delete
    suspend fun delete(scheduleDid: ScheduleDid)

    @Query("SELECT * FROM schedule_did WHERE id = :id")
    suspend fun getById(id: Int): ScheduleDid?

    @Query("SELECT * FROM schedule_did ORDER BY name ASC")
    fun getAllFlow(): Flow<List<ScheduleDid>>

    @Query("DELETE FROM schedule_did WHERE id = :id")
    suspend fun deleteById(id: Int)
}