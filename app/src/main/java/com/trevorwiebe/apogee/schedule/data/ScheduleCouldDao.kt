package com.trevorwiebe.apogee.schedule.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleCouldDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scheduleCould: ScheduleCould)

    @Update
    suspend fun update(scheduleCould: ScheduleCould)

    @Delete
    suspend fun delete(scheduleCould: ScheduleCould)

    @Query("SELECT * FROM schedule_could WHERE id = :id")
    suspend fun getById(id: Int): ScheduleCould?

    @Query("SELECT * FROM schedule_could ORDER BY name ASC")
    fun getAllFlow(): Flow<List<ScheduleCould>>

    @Query("DELETE FROM schedule_could WHERE id = :id")
    suspend fun deleteById(id: Int)
}