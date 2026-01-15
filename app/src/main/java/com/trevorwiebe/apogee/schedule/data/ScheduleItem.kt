package com.trevorwiebe.apogee.schedule.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "schedule_items")
data class ScheduleItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val dayOfWeek: Int
)
