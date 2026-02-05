package com.trevorwiebe.apogee.logtime.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "schedule_did",
    indices = [Index(value = ["startDateTime"], unique = true)]
)
data class Did(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val scheduleCouldId: Int,
    val startDateTime: LocalDateTime
)
