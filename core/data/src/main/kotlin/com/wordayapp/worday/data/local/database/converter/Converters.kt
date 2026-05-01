package com.wordayapp.worday.data.local.database.converter

import androidx.room.TypeConverter
import com.wordayapp.worday.domain.model.WordLevel
import com.wordayapp.worday.domain.model.WordType

class Converters {

    @TypeConverter
    fun fromWordLevel(level: WordLevel): String = level.name

    @TypeConverter
    fun toWordLevel(value: String): WordLevel = WordLevel.valueOf(value)

    @TypeConverter
    fun fromWordType(type: WordType): String = type.name

    @TypeConverter
    fun toWordType(value: String): WordType = WordType.valueOf(value)
}