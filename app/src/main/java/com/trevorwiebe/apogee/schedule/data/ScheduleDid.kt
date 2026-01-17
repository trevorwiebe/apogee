package com.trevorwiebe.apogee.schedule.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_did")
data class ScheduleDid(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)