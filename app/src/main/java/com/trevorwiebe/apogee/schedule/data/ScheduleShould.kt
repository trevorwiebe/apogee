package com.trevorwiebe.apogee.schedule.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    tableName = "schedule_should",
    indices = [Index(value = ["dayOfWeek", "startTime"], unique = true)]
)
data class ScheduleShould(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val scheduleCouldId: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val dayOfWeek: Int
)
