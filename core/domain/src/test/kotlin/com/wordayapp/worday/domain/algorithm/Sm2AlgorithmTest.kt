package com.wordayapp.worday.domain.algorithm

import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.model.WordLevel
import com.wordayapp.worday.domain.model.WordType
import com.wordayapp.worday.domain.repository.WordRepository
import com.wordayapp.worday.domain.usecase.UpdateWordAfterQuizUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class Sm2AlgorithmTest {

    private fun word(
        repetitionCount: Int = 0,
        easeFactor: Float = 2.5f,
        intervalDays: Int = 1,
        isLearned: Boolean = false,
        nextReviewDate: Long = 0L
    ) = Word(
        id = 42,
        english = "resilient",
        turkish = "dayanıklı",
        definition = "able to recover quickly",
        level = WordLevel.B2,
        type = WordType.ADJECTIVE,
        ipa = "/rɪˈzɪliənt/",
        exampleSentence1 = "She is resilient.",
        exampleSentence2 = "A resilient material.",
        repetitionCount = repetitionCount,
        easeFactor = easeFactor,
        intervalDays = intervalDays,
        isLearned = isLearned,
        nextReviewDate = nextReviewDate
    )

    @Test
    fun `first successful review schedules one day`() {
        val result = Sm2Algorithm.calculate(word(), quality = 4)

        assertEquals(1, result.repetitionCount)
        assertEquals(1, result.intervalDays)
        assertEquals(2.5f, result.easeFactor, 0.0001f)
        assertFalse(result.isLearned)
        assertTrue(result.nextReviewDate > System.currentTimeMillis())
    }

    @Test
    fun `second successful review schedules six days`() {
        val result = Sm2Algorithm.calculate(word(repetitionCount = 1, intervalDays = 1), quality = 4)

        assertEquals(2, result.repetitionCount)
        assertEquals(6, result.intervalDays)
    }

    @Test
    fun `repeated incorrect answers reset repetition and keep ease factor above minimum`() {
        var current = word(repetitionCount = 4, easeFactor = 1.31f, intervalDays = 40, isLearned = true)

        repeat(5) {
            current = Sm2Algorithm.calculate(current, quality = 1)
        }

        assertEquals(0, current.repetitionCount)
        assertEquals(1, current.intervalDays)
        assertTrue(current.easeFactor >= 1.3f)
        assertFalse(current.isLearned)
    }

    @Test
    fun `quality below three is a failure`() {
        val result = Sm2Algorithm.calculate(word(repetitionCount = 2, intervalDays = 6), quality = 2)

        assertEquals(0, result.repetitionCount)
        assertEquals(1, result.intervalDays)
    }

    @Test
    fun `quality three is accepted as successful recall`() {
        val result = Sm2Algorithm.calculate(word(), quality = 3)

        assertEquals(1, result.repetitionCount)
        assertEquals(1, result.intervalDays)
        assertTrue(result.easeFactor >= 1.3f)
    }

    @Test
    fun `invalid quality is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            Sm2Algorithm.calculate(word(), quality = -1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            Sm2Algorithm.calculate(word(), quality = 6)
        }
    }

    @Test
    fun `third successful review marks word as learned`() {
        val result = Sm2Algorithm.calculate(
            word(repetitionCount = 2, intervalDays = 6),
            quality = 4
        )

        assertEquals(3, result.repetitionCount)
        assertTrue(result.isLearned)
        assertTrue(result.intervalDays >= 1)
    }

    @Test
    fun `overdue word after long absence can be reviewed and receives a new future due date`() {
        val oneHundredDaysAgo = System.currentTimeMillis() - 100L * 24 * 60 * 60 * 1000
        val overdue = word(repetitionCount = 3, intervalDays = 15, nextReviewDate = oneHundredDaysAgo)

        val result = Sm2Algorithm.calculate(overdue, quality = 4)

        assertTrue(overdue.nextReviewDate < System.currentTimeMillis())
        assertEquals(4, result.repetitionCount)
        assertTrue(result.nextReviewDate > System.currentTimeMillis())
    }

    @Test
    fun `boolean overload maps correct and incorrect answers to quality four and one`() {
        val correct = Sm2Algorithm.calculate(word(), isCorrect = true)
        val incorrect = Sm2Algorithm.calculate(word(repetitionCount = 2), isCorrect = false)

        assertEquals(1, correct.repetitionCount)
        assertEquals(0, incorrect.repetitionCount)
        assertEquals(1, incorrect.intervalDays)
    }

    @Test
    fun `use case persists calculated word state`() = runTest {
        val repository = mockk<WordRepository>(relaxed = true)
        val useCase = UpdateWordAfterQuizUseCase(repository)
        val input = word(repetitionCount = 1)

        val updated = useCase(input, isCorrect = true)

        assertEquals(2, updated.repetitionCount)
        coVerify(exactly = 1) { repository.updateWord(updated) }
    }
}
