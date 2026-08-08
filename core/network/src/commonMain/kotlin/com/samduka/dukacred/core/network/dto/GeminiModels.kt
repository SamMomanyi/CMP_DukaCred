package com.samduka.dukacred.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    @SerialName("contents") val contents: List<GeminiContent>,
    @SerialName("generationConfig") val generationConfig: GenerationConfig,
)

@Serializable
data class GeminiContent(
    @SerialName("parts") val parts: List<GeminiPart>,
)


@Serializable
data class GeminiPart(
    @SerialName("text") val text: String? = null,
    @SerialName("inlineData") val inlineData: InlineData? = null,
)

@Serializable
data class InlineData(
    @SerialName("mimeType") val mimeType: String = "image/jpeg",
    @SerialName("data") val data: String, // Base64 string
)

@Serializable
data class GenerationConfig(
    @SerialName("responseMimeType") val responseMimeType: String = "application/json",
)

// ── Response models ──────────────────────────────────────────────────────────

@Serializable
data class GeminiResponse(
    @SerialName("candidates") val candidates: List<Candidate>? = null,
)

@Serializable
data class Candidate(
    @SerialName("content") val content: CandidateContent? = null,
)

@Serializable
data class CandidateContent(
    @SerialName("parts") val parts: List<TextPartResponse>? = null,
)

@Serializable
data class TextPartResponse(
    @SerialName("text") val text: String? = null,
)