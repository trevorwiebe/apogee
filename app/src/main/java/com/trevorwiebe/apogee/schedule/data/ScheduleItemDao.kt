package com.trevorwiebe.apogee.schedule.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scheduleItem: ScheduleItem)

    @Update
    suspend fun update(scheduleItem: ScheduleItem)

    @Delete
    suspend fun delete(scheduleItem: ScheduleItem)

    @Query("SELECT * FROM schedule_items WHERE id = :id")
    suspend fun getById(id: String): ScheduleItem?

    @Query("SELECT * FROM schedule_items ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllFlow(): Flow<List<ScheduleItem>>

    @Query("SELECT * FROM schedule_items WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getByDayOfWeekFlow(dayOfWeek: Int): Flow<List<ScheduleItem>>

    @Query("DELETE FROM schedule_items WHERE id = :id")
    suspend fun deleteById(id: String)
}
