package com.trevorwiebe.apogee.global.data

import com.trevorwiebe.apogee.global.domain.usecases.CreateWeekFifteenIncrements
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

}