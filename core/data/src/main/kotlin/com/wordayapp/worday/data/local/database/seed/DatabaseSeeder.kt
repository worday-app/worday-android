package com.wordayapp.worday.data.local.database.seed

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wordayapp.worday.data.local.database.dao.WordDao
import com.wordayapp.worday.data.local.database.entity.WordEntity

object DatabaseSeeder {

    suspend fun seed(context: Context, wordDao: WordDao) {
        if (wordDao.getWordCount() > 0) return  // zaten dolu

        val json = context.assets.open("words.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<WordJsonModel>>() {}.type
        val words: List<WordJsonModel> = Gson().fromJson(json, type)

        val entities = words.mapIndexed { index, word ->
            WordEntity(
                id = index + 1,
                english = word.english,
                turkish = word.turkish,
                definition = word.definition,
                ipa = word.ipa,
                exampleSentence1 = word.examples.getOrElse(0) { "" },
                exampleSentence2 = word.examples.getOrElse(1) { "" },
                level = word.level,
                type = mapPartOfSpeech(word.partOfSpeech)
            )
        }

        wordDao.insertAll(entities)
    }

    private fun mapPartOfSpeech(pos: String): String = when (pos.lowercase()) {
        "verb" -> "VERB"
        "noun" -> "NOUN"
        "adjective" -> "ADJECTIVE"
        "adverb" -> "ADVERB"
        "preposition" -> "PREPOSITION"
        "conjunction" -> "CONJUNCTION"
        "pronoun" -> "PRONOUN"
        "exclamation" -> "EXCLAMATION"
        else -> "OTHER"
    }
}