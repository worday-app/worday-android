package com.wordayapp.worday.data.local.database.seed

import java.util.Locale
import java.util.zip.CRC32

internal object WordCatalogIdentity {

    fun catalogId(word: WordJsonModel): String {
        require(word.id.isNotBlank()) { "Word catalog entry has an empty id." }
        require(word.level.isNotBlank()) { "Word catalog entry has an empty level." }

        return word.id.trim() + ":" + word.level.trim().uppercase(Locale.ROOT)
    }

    fun stableDatabaseId(catalogId: String): Int {
        val checksum = CRC32().apply {
            update(catalogId.toByteArray(Charsets.UTF_8))
        }
        return (checksum.value % Int.MAX_VALUE).toInt() + 1
    }

    fun validate(words: List<WordJsonModel>) {
        require(words.isNotEmpty()) { "Word catalog is empty." }

        val catalogIds = words.map(::catalogId)
        require(catalogIds.size == catalogIds.distinct().size) {
            "Word catalog contains duplicate id and level combinations."
        }

        val databaseIds = catalogIds.groupBy(::stableDatabaseId)
        require(databaseIds.values.none { ids -> ids.distinct().size > 1 }) {
            "Word catalog contains a stable database ID collision."
        }
    }
}
