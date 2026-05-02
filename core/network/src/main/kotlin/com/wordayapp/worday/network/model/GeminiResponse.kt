package com.wordayapp.worday.network.model

import com.google.gson.annotations.SerializedName

data class GeminiResponse(
    @SerializedName("candidates")
    val candidates: List<GeminiCandidate>? = null,
    @SerializedName("error")
    val error: GeminiError? = null
)

data class GeminiCandidate(
    @SerializedName("content")
    val content: GeminiContent? = null,
    @SerializedName("finishReason")
    val finishReason: String? = null
)

data class GeminiError(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)

/**
 * Response'tan ilk text bloğunu çeker. Null dönerse boş string.
 */
fun GeminiResponse.extractText(): String =
    candidates
        ?.firstOrNull()
        ?.content
        ?.parts
        ?.firstOrNull()
        ?.text
        .orEmpty()
        .trim()