package com.samduka.dukacred.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    @SerialName("contents") val contents: List<GeminiContent>,       // FIX: was `List` (no type arg)
    @SerialName("generationConfig") val generationConfig: GenerationConfig,
)

@Serializable
data class GeminiContent(
    @SerialName("parts") val parts: List<GeminiPart>,                // FIX: was `List`
)

// FIX: was `sealed interface GeminiPart` with `TextPart`/`InlineDataPart`
// subtypes. Polymorphic serialization on a sealed type injects a "type"
// discriminator into the JSON — e.g. {"type": "...TextPart", "text": "..."}.
// Gemini's REST API has no idea what "type" means here; it expects a plain
// part object with either a "text" key or an "inlineData" key, nothing
// else. A flat class with two nullable fields produces exactly that shape —
// whichever field is left null is simply omitted from the output.
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
    @SerialName("candidates") val candidates: List<Candidate>? = null, // FIX: was `List?`
)

@Serializable
data class Candidate(
    @SerialName("content") val content: CandidateContent? = null,
)

@Serializable
data class CandidateContent(
    @SerialName("parts") val parts: List<TextPartResponse>? = null,   // FIX: was `List?`
)

@Serializable
data class TextPartResponse(
    @SerialName("text") val text: String? = null,
)