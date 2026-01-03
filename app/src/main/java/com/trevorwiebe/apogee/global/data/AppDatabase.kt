package com.trevorwiebe.apogee.global.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.trevorwiebe.apogee.schedule.data.ScheduleItem
import com.trevorwiebe.apogee.schedule.data.ScheduleItemDao

@Database(
    entities = [ScheduleItem::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(LocalTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scheduleItemDao(): ScheduleItemDao
}
