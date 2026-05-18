package com.wordayapp.worday.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wordayapp.worday.data.local.database.converter.Converters
import com.wordayapp.worday.data.local.database.dao.DailySessionDao
import com.wordayapp.worday.data.local.database.dao.UserProgressDao
import com.wordayapp.worday.data.local.database.dao.WordDao
import com.wordayapp.worday.data.local.database.entity.DailySessionEntity
import com.wordayapp.worday.data.local.database.entity.UserProgressEntity
import com.wordayapp.worday.data.local.database.entity.WordEntity

@Database(
    entities = [
        WordEntity::class,
        UserProgressEntity::class,
        DailySessionEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class WordayDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun dailySessionDao(): DailySessionDao
}