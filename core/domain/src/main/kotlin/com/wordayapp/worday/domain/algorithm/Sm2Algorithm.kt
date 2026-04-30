package com.wordayapp.worday.domain.algorithm

import com.wordayapp.worday.domain.model.Word
import java.util.concurrent.TimeUnit

/**
 * SM-2 Spaced Repetition Algorithm
 *
 * Quality scale:
 * 0 - complete blackout
 * 1 - incorrect, but correct answer felt familiar
 * 2 - incorrect, but correct answer was easy to recall
 * 3 - correct with significant difficulty
 * 4 - correct after hesitation
 * 5 - perfect response
 */
object Sm2Algorithm {

    private const val MIN_EASE_FACTOR = 1.3f
    private const val DEFAULT_EASE_FACTOR = 2.5f

    fun calculate(word: Word, isCorrect: Boolean): Word {
        val quality = if (isCorrect) 4 else 1
        return calculate(word, quality)
    }

    fun calculate(word: Word, quality: Int): Word {
        require(quality in 0..5) { "Quality must be between 0 and 5" }

        val newEaseFactor = calculateEaseFactor(word.easeFactor, quality)
        val newRepetitionCount: Int
        val newIntervalDays: Int

        if (quality < 3) {
            // Incorrect answer — reset repetitions
            newRepetitionCount = 0
            newIntervalDays = 1
        } else {
            // Correct answer — advance
            newRepetitionCount = word.repetitionCount + 1
            newIntervalDays = when (word.repetitionCount) {
                0 -> 1
                1 -> 6
                else -> (word.intervalDays * newEaseFactor).toInt()
            }
        }

        val nextReviewDate = System.currentTimeMillis() +
                TimeUnit.DAYS.toMillis(newIntervalDays.toLong())

        return word.copy(
            repetitionCount = newRepetitionCount,
            easeFactor = newEaseFactor,
            intervalDays = newIntervalDays,
            nextReviewDate = nextReviewDate,
            isLearned = newRepetitionCount >= 3,
        )
    }

    private fun calculateEaseFactor(currentEaseFactor: Float, quality: Int): Float {
        val newFactor = currentEaseFactor +
                (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)).toFloat()
        return newFactor.coerceAtLeast(MIN_EASE_FACTOR)
    }
}