package com.trevorwiebe.apogee.global.data

import android.content.Context
import androidx.room.Room
import com.trevorwiebe.apogee.logtime.data.DidDao
import com.trevorwiebe.apogee.schedule.data.ScheduleCouldDao
import com.trevorwiebe.apogee.schedule.data.ScheduleShouldDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "apogee_database"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideScheduleItemDao(database: AppDatabase): ScheduleShouldDao {
        return database.scheduleItemDao()
    }

    @Provides
    @Singleton
    fun provideScheduleCouldDao(database: AppDatabase): ScheduleCouldDao {
        return database.scheduleCouldDao()
    }

    @Provides
    @Singleton
    fun provideDidDao(database: AppDatabase): DidDao {
        return database.didDao()
    }
}
