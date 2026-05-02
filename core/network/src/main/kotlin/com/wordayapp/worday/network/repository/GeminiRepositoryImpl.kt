package com.wordayapp.worday.network.repository

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
        level: String
    ): GeminiResult {

        if (!networkObserver.isConnected) {
            return GeminiResult.NoInternet
        }

        return try {
            val prompt = buildPrompt(word, level)
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )

            val response = apiService.generateContent(
                apiKey = apiKey,
                request = request
            )

            when {
                response.isSuccessful -> {
                    val body = response.body()

                    // API'nin kendi hata alanı (HTTP 200 ama hata içeren response)
                    if (body?.error != null) {
                        return GeminiResult.Error(
                            code = body.error.code,
                            message = body.error.message
                        )
                    }

                    val text = body?.extractText()
                    if (text.isNullOrBlank()) {
                        GeminiResult.Error(message = "Boş yanıt alındı")
                    } else {
                        val sentences = parseSentences(text)
                        GeminiResult.Success(sentences)
                    }
                }

                response.code() == 429 -> GeminiResult.QuotaExceeded

                response.code() in 500..599 -> GeminiResult.Error(
                    code = response.code(),
                    message = "Sunucu hatası, lütfen tekrar dene"
                )

                else -> GeminiResult.Error(
                    code = response.code(),
                    message = response.message()
                )
            }

        } catch (e: java.net.UnknownHostException) {
            GeminiResult.NoInternet
        } catch (e: java.net.SocketTimeoutException) {
            GeminiResult.Error(message = "Bağlantı zaman aşımına uğradı")
        } catch (e: Exception) {
            GeminiResult.Error(message = e.localizedMessage ?: "Beklenmedik bir hata oluştu")
        }
    }

    /**
     * Cevaptaki cümleleri satır bazlı ayırır.
     * "1. sentence" veya "- sentence" formatlarını normalize eder.
     */
    private fun parseSentences(raw: String): List<String> =
        raw.lines()
            .map { line ->
                line.trimStart()
                    .removePrefix("1.").removePrefix("2.").removePrefix("3.")
                    .removePrefix("-").removePrefix("•")
                    .trim()
            }
            .filter { it.isNotBlank() }
            .take(3)   // maksimum 3 cümle

    private fun buildPrompt(word: String, level: String): String = """
        Generate 3 natural English example sentences for the word "$word".
        The sentences should be appropriate for a $level English learner.
        Keep each sentence concise and clear.
        Return ONLY the sentences, one per line, without numbering or bullet points.
    """.trimIndent()
}