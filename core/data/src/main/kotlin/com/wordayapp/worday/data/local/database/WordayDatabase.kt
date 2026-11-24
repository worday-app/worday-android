package com.wordayapp.worday.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wordayapp.worday.data.local.database.converter.Converters
import com.wordayapp.worday.data.local.database.dao.CatalogMetadataDao
import com.wordayapp.worday.data.local.database.dao.DailySessionDao
import com.wordayapp.worday.data.local.database.dao.UserProgressDao
import com.wordayapp.worday.data.local.database.dao.WordDao
import com.wordayapp.worday.data.local.database.entity.CatalogMetadataEntity
import com.wordayapp.worday.data.local.database.entity.DailySessionEntity
import com.wordayapp.worday.data.local.database.entity.UserProgressEntity
import com.wordayapp.worday.data.local.database.entity.WordEntity

@Database(
    entities = [
        WordEntity::class,
        UserProgressEntity::class,
        DailySessionEntity::class,
        CatalogMetadataEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class WordayDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun dailySessionDao(): DailySessionDao
    abstract fun catalogMetadataDao(): CatalogMetadataDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE words ADD COLUMN catalogId TEXT")
        database.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_words_catalogId ON words (catalogId)"
        )
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS catalog_metadata (
                id INTEGER NOT NULL,
                version INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
    }
}
