package com.wordayapp.worday.data.local.database.seed

import android.content.Context
import androidx.room.withTransaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wordayapp.worday.data.local.database.WordayDatabase
import com.wordayapp.worday.data.local.database.entity.CatalogMetadataEntity
import com.wordayapp.worday.data.local.database.entity.WordEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale

sealed interface SeedStatus {
    data object Pending : SeedStatus
    data object Loading : SeedStatus
    data object Ready : SeedStatus
    data class Failed(val cause: Throwable) : SeedStatus
}

object SeedState {
    private val mutableStatus = MutableStateFlow<SeedStatus>(SeedStatus.Pending)
    val status = mutableStatus

    fun markLoading() {
        mutableStatus.value = SeedStatus.Loading
    }

    fun markReady() {
        mutableStatus.value = SeedStatus.Ready
    }

    fun markFailed(cause: Throwable) {
        mutableStatus.value = SeedStatus.Failed(cause)
    }

    suspend fun awaitReady() {
        when (val result = mutableStatus.first {
            it is SeedStatus.Ready || it is SeedStatus.Failed
        }) {
            SeedStatus.Ready -> Unit
            is SeedStatus.Failed -> throw IllegalStateException(
                "Word catalog initialization failed.",
                result.cause
            )
            else -> error("Unexpected word catalog state.")
        }
    }
}

object DatabaseSeeder {

    private const val CATALOG_VERSION = 1
    private val mutex = Mutex()

    suspend fun seed(context: Context, database: WordayDatabase) = mutex.withLock {
        if (SeedState.status.value is SeedStatus.Ready) return@withLock

        SeedState.markLoading()

        try {
            val currentVersion = database.catalogMetadataDao().getVersion() ?: 0
            if (currentVersion >= CATALOG_VERSION) {
                SeedState.markReady()
                return@withLock
            }

            val json = context.assets.open("words.json")
                .bufferedReader()
                .use { it.readText() }

            val type = object : TypeToken<List<WordJsonModel>>() {}.type
            val words: List<WordJsonModel> =
                Gson().fromJson<List<WordJsonModel>>(json, type)
                    ?: throw IllegalStateException("words.json does not contain a word list.")

            WordCatalogIdentity.validate(words)

            database.withTransaction {
                val metadataDao = database.catalogMetadataDao()
                val storedVersion = metadataDao.getVersion() ?: 0

                if (storedVersion < CATALOG_VERSION) {
                    val wordDao = database.wordDao()
                    val existingWords = wordDao.getAllWords()

                    val existingByCatalogId = existingWords
                        .filter { it.catalogId != null }
                        .associateBy { it.catalogId!! }

                    val legacyByKey = existingWords
                        .filter { it.catalogId == null }
                        .groupBy { legacyKey(it.english, it.level, it.type) }

                    val usedIds = existingWords.associateBy { it.id }.toMutableMap()
                    val newWords = mutableListOf<WordEntity>()

                    words.forEach { word ->
                        val catalogId = WordCatalogIdentity.catalogId(word)
                        val existing = existingByCatalogId[catalogId]
                            ?: legacyByKey[legacyKey(
                                word.english,
                                word.level,
                                mapPartOfSpeech(word.partOfSpeech)
                            )]?.let { matches ->
                                check(matches.size == 1) {
                                    "Cannot safely match legacy word: " + word.english
                                }
                                matches.single()
                            }

                        if (existing != null) {
                            if (existing.catalogId == null) {
                                val updatedRows = wordDao.setCatalogId(existing.id, catalogId)
                                check(updatedRows == 1) {
                                    "Could not assign catalog identity to word: " + word.english
                                }
                            }
                        } else {
                            val stableId = WordCatalogIdentity.stableDatabaseId(catalogId)
                            check(usedIds[stableId] == null) {
                                "Stable word ID conflicts with an existing record: " + catalogId
                            }

                            val entity = WordEntity(
                                id = stableId,
                                english = word.english,
                                turkish = word.turkish,
                                definition = word.definition,
                                ipa = word.ipa,
                                exampleSentence1 = word.examples.getOrElse(0) { "" },
                                exampleSentence2 = word.examples.getOrElse(1) { "" },
                                level = word.level,
                                type = mapPartOfSpeech(word.partOfSpeech),
                                catalogId = catalogId
                            )
                            newWords += entity
                            usedIds[stableId] = entity
                        }
                    }

                    if (newWords.isNotEmpty()) {
                        wordDao.insertAll(newWords)
                    }

                    metadataDao.setVersion(CatalogMetadataEntity(version = CATALOG_VERSION))
                }
            }

            SeedState.markReady()
        } catch (error: Exception) {
            SeedState.markFailed(error)
            throw error
        }
    }

    private fun legacyKey(english: String, level: String, type: String): String =
        english.trim().lowercase(Locale.ROOT) + "|" +
            level.trim().uppercase(Locale.ROOT) + "|" +
            type.trim().uppercase(Locale.ROOT)

    private fun mapPartOfSpeech(pos: String): String = when (pos.lowercase(Locale.ROOT)) {
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
