package com.sanju.expensetracker.di

import android.content.Context
import com.sanju.expensetracker.data.preferences.SettingsPreferences
import com.sanju.expensetracker.data.repository.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideExpenseRepository(
        @ApplicationContext context: Context
    ): ExpenseRepository {
        return ExpenseRepository(context)
    }

    @Provides
    @Singleton
    fun provideSettingsPreferences(
        @ApplicationContext context: Context
    ): SettingsPreferences {
        return SettingsPreferences(context)
    }
}