package com.trevorwiebe.apogee.schedule.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleShouldDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scheduleShould: ScheduleShould)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scheduleShouldList: List<ScheduleShould>)

    @Update
    suspend fun update(scheduleShould: ScheduleShould)

    @Delete
    suspend fun delete(scheduleShould: ScheduleShould)

    @Query("SELECT * FROM schedule_should WHERE id = :id")
    suspend fun getById(id: String): ScheduleShould?

    @Query("SELECT * FROM schedule_should ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllFlow(): Flow<List<ScheduleShould>>

    @Query("SELECT * FROM schedule_should WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getByDayOfWeekFlow(dayOfWeek: Int): Flow<List<ScheduleShould>>

    @Query("DELETE FROM schedule_should WHERE id = :id")
    suspend fun deleteById(id: String)
}
