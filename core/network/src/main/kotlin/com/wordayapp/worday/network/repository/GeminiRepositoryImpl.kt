package com.wordayapp.worday.network.repository

import com.wordayapp.worday.domain.model.WordayError
import com.wordayapp.worday.domain.repository.GeminiRepository
import com.wordayapp.worday.domain.repository.GeminiResult
import com.wordayapp.worday.network.api.GeminiApiService
import com.wordayapp.worday.network.model.GeminiContent
import com.wordayapp.worday.network.model.GeminiPart
import com.wordayapp.worday.network.model.GeminiRequest
import com.wordayapp.worday.network.model.extractText
import com.wordayapp.worday.network.observer.NetworkObserver
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GeminiRepositoryImpl @Inject constructor(
    private val apiService: GeminiApiService,
    private val networkObserver: NetworkObserver,
    @Named("gemini_api_key") private val apiKey: String
) : GeminiRepository {

    override suspend fun getExampleSentences(
        word: String,
        count: Int
    ): GeminiResult {

        if (!networkObserver.isConnected) {
            return GeminiResult.NoInternet
        }

        return try {

            val prompt = buildPrompt(word, count)

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(text = prompt)
                        )
                    )
                )
            )

            val response = apiService.generateContent(
                apiKey = apiKey,
                request = request
            )

            when {
                response.isSuccessful -> {

                    val body = response.body()

                    if (body?.error != null) {
                        return GeminiResult.Error(
                            WordayError.Network(
                                code = body.error.code,
                                message = body.error.message
                            )
                        )
                    }

                    val text = body?.extractText()

                    if (text.isNullOrBlank()) {
                        GeminiResult.Error(
                            WordayError.Unknown
                        )
                    } else {
                        GeminiResult.Success(
                            parseSentences(text)
                        )
                    }
                }

                response.code() == 429 -> {
                    GeminiResult.QuotaExceeded
                }

                response.code() in 500..599 -> {
                    GeminiResult.Error(
                        WordayError.Network(
                            code = response.code(),
                            message = "Server error"
                        )
                    )
                }

                else -> {
                    GeminiResult.Error(
                        WordayError.Network(
                            code = response.code(),
                            message = response.message()
                        )
                    )
                }
            }

        } catch (_: java.net.UnknownHostException) {

            GeminiResult.NoInternet

        } catch (_: java.net.SocketTimeoutException) {

            GeminiResult.Error(
                WordayError.Network(
                    message = "Connection timeout"
                )
            )

        } catch (e: Exception) {

            GeminiResult.Error(
                WordayError.Unknown
            )
        }
    }

    override suspend fun getRemainingFreeRequests(): Int {
        // Faz 2 — DataStore
        return 2
    }

    override suspend fun consumeRequest() {
        // Faz 2 — DataStore decrement
    }

    private fun parseSentences(raw: String): List<String> =
        raw.lines()
            .map {
                it.trim()
                    .removePrefix("-")
                    .removePrefix("•")
                    .trim()
            }
            .filter { it.isNotBlank() }

    private fun buildPrompt(
        word: String,
        count: Int
    ): String = """
        Generate $count natural English example sentences
        for the word "$word".

        Return only the sentences.
        One sentence per line.
    """.trimIndent()
}