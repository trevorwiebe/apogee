package com.trevorwiebe.apogee.logtime.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_did")
data class Did(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)