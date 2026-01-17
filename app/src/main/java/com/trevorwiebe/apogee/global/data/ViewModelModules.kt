package com.trevorwiebe.apogee.global.data

import com.trevorwiebe.apogee.global.domain.usecases.CreateWeekFifteenIncrements
import com.trevorwiebe.apogee.schedule.data.ScheduleShouldDao
import com.trevorwiebe.apogee.schedule.domain.usecases.GetScheduledShould
import com.trevorwiebe.apogee.schedule.domain.usecases.SaveScheduleShould
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModules {

    @Provides
    @ViewModelScoped
    fun providesCreateWeekFifteenIncrements(): CreateWeekFifteenIncrements {
        return CreateWeekFifteenIncrements()
    }

    @Provides
    @ViewModelScoped
    fun providesSaveScheduleItem(
        scheduleShouldDao: ScheduleShouldDao
    ): SaveScheduleShould {
        return SaveScheduleShould(scheduleShouldDao)
    }

    @Provides
    @ViewModelScoped
    fun provideGetScheduleItems(
        scheduleShouldDao: ScheduleShouldDao
    ): GetScheduledShould{
        return GetScheduledShould(scheduleShouldDao = scheduleShouldDao)
    }
}