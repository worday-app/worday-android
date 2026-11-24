package com.wordayapp.worday.data.local.database.seed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class WordCatalogIdentityTest {

    @Test
    fun stableDatabaseId_isDeterministic() {
        val catalogId = "lie_verb:B1"

        assertEquals(
            WordCatalogIdentity.stableDatabaseId(catalogId),
            WordCatalogIdentity.stableDatabaseId(catalogId)
        )
    }

    @Test
    fun sameSourceIdAtDifferentLevels_hasDifferentCatalogIds() {
        val beginner = word(id = "lie_verb", level = "A1")
        val intermediate = word(id = "lie_verb", level = "B1")

        assertNotEquals(
            WordCatalogIdentity.catalogId(beginner),
            WordCatalogIdentity.catalogId(intermediate)
        )
        assertNotEquals(
            WordCatalogIdentity.stableDatabaseId(WordCatalogIdentity.catalogId(beginner)),
            WordCatalogIdentity.stableDatabaseId(WordCatalogIdentity.catalogId(intermediate))
        )
    }

    @Test
    fun validate_rejectsDuplicateCatalogIdentity() {
        val entry = word(id = "same_word", level = "A1")

        assertThrows(IllegalArgumentException::class.java) {
            WordCatalogIdentity.validate(listOf(entry, entry.copy(english = "different")))
        }
    }

    @Test
    fun stableDatabaseId_isPositive() {
        val id = WordCatalogIdentity.stableDatabaseId("example_noun:A2")

        assert(id > 0)
    }

    private fun word(id: String, level: String) = WordJsonModel(
        id = id,
        english = "example",
        turkish = "örnek",
        definition = "A test entry.",
        level = level,
        partOfSpeech = "noun",
        ipa = "/ɪɡˈzɑːmpəl/"
    )
}
