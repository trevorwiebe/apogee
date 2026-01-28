package com.trevorwiebe.apogee.global.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.trevorwiebe.apogee.schedule.data.ScheduleCould
import com.trevorwiebe.apogee.schedule.data.ScheduleCouldDao
import com.trevorwiebe.apogee.logtime.data.Did
import com.trevorwiebe.apogee.logtime.data.DidDao
import com.trevorwiebe.apogee.schedule.data.ScheduleShould
import com.trevorwiebe.apogee.schedule.data.ScheduleShouldDao

@Database(
    entities = [ScheduleShould::class, Did::class, ScheduleCould::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(LocalTimeConverter::class, ColorConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scheduleItemDao(): ScheduleShouldDao
    abstract fun scheduleCouldDao(): ScheduleCouldDao
    abstract fun didDao(): DidDao
}
