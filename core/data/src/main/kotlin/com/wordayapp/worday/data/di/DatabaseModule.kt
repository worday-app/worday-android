package com.wordayapp.worday.data.di

import android.content.Context
import androidx.room.Room
import com.wordayapp.worday.data.local.database.WordayDatabase
import com.wordayapp.worday.data.local.database.dao.DailySessionDao
import com.wordayapp.worday.data.local.database.dao.UserProgressDao
import com.wordayapp.worday.data.local.database.dao.WordDao
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
    fun provideWordayDatabase(
        @ApplicationContext context: Context
    ): WordayDatabase = Room.databaseBuilder(
        context,
        WordayDatabase::class.java,
        "worday.db"
    ).build()

    @Provides
    fun provideWordDao(db: WordayDatabase): WordDao = db.wordDao()

    @Provides
    fun provideUserProgressDao(db: WordayDatabase): UserProgressDao = db.userProgressDao()

    @Provides
    fun provideDailySessionDao(db: WordayDatabase): DailySessionDao = db.dailySessionDao()
}