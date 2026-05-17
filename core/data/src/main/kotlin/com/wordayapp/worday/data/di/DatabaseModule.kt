package com.wordayapp.worday.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wordayapp.worday.data.local.database.WordayDatabase
import com.wordayapp.worday.data.local.database.dao.DailySessionDao
import com.wordayapp.worday.data.local.database.dao.UserProgressDao
import com.wordayapp.worday.data.local.database.dao.WordDao
import com.wordayapp.worday.data.local.database.seed.DatabaseSeeder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWordayDatabase(
        @ApplicationContext context: Context
    ): WordayDatabase {
        lateinit var database: WordayDatabase
        database = Room.databaseBuilder(
            context,
            WordayDatabase::class.java,
            "worday.db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        DatabaseSeeder.seed(context, database.wordDao())
                    }
                }
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    // DB zaten vardı, seed gerekmez — direkt sinyal ver
                    CoroutineScope(Dispatchers.IO).launch {
                        DatabaseSeeder.seed(context, database.wordDao())
                    }
                }
            })
            .build()

        return database
    }

    @Provides
    fun provideWordDao(db: WordayDatabase): WordDao = db.wordDao()

    @Provides
    fun provideUserProgressDao(db: WordayDatabase): UserProgressDao = db.userProgressDao()

    @Provides
    fun provideDailySessionDao(db: WordayDatabase): DailySessionDao = db.dailySessionDao()
}