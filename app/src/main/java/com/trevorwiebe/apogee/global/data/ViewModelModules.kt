package com.trevorwiebe.apogee.global.data

import com.trevorwiebe.apogee.global.domain.usecases.CreateWeekFifteenIncrements
import com.trevorwiebe.apogee.schedule.data.ScheduleItemDao
import com.trevorwiebe.apogee.schedule.domain.usecases.SaveScheduleItem
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
    fun providesSaveScheduleItemUseCase(
        scheduleItemDao: ScheduleItemDao
    ): SaveScheduleItem {
        return SaveScheduleItem(scheduleItemDao)
    }
}