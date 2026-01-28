package com.trevorwiebe.apogee.schedule.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "schedule_should")
data class ScheduleShould(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val scheduleCouldId: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val dayOfWeek: Int
)
