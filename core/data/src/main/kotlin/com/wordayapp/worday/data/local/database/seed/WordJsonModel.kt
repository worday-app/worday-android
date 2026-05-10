package com.wordayapp.worday.data.local.database.seed


import com.google.gson.annotations.SerializedName

data class WordJsonModel(
    @SerializedName("id") val id: String,
    @SerializedName("english") val english: String,
    @SerializedName("turkish") val turkish: String,
    @SerializedName("definition") val definition: String,
    @SerializedName("level") val level: String,
    @SerializedName("partOfSpeech") val partOfSpeech: String,
    @SerializedName("ipa") val ipa: String,
    @SerializedName("examples") val examples: List<String> = emptyList()
)