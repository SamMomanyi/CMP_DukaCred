package com.samduka.dukacred.core.network.api

import com.samduka.dukacred.core.network.BuildKonfig
import com.samduka.dukacred.core.network.dto.GenerationConfig
import com.samduka.dukacred.core.network.dto.GeminiContent
import com.samduka.dukacred.core.network.dto.GeminiPart
import com.samduka.dukacred.core.network.dto.GeminiRequest
import com.samduka.dukacred.core.network.dto.GeminiResponse
import com.samduka.dukacred.core.network.dto.InlineData
import com.samduka.dukacred.core.network.model.InvoiceExtractionResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class GeminiInvoiceApi(
    private val httpClient: HttpClient,
    private val json: Json,
) {
    suspend fun extractInvoiceDetails(base64Image: String): InvoiceExtractionResult {
        val apiKey = BuildKonfig.GEMINI_API_KEY
        val url =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val prompt = """
            Extract the details from this receipt or invoice image into a JSON object.
            Analyze Kenyan specific terms like 'KSh', 'M-Pesa', 'Till Number', 'ETR', or 'VAT'.
            Return strictly valid JSON with fields:
            - merchant_name (string)
            - total_amount (number)
            - vat_amount (number)
            - currency (string, e.g. KES)
            - invoice_date (string)
            - invoice_number (string)
            - line_items (array of objects with description, quantity, unit_price, total_price)
        """.trimIndent()

        val requestPayload = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        // FIX: GeminiPart is now a flat data class, not a sealed
                        // type — construct via named params instead of
                        // GeminiPart.TextPart(...) / GeminiPart.InlineDataPart(...)
                        GeminiPart(text = prompt),
                        GeminiPart(inlineData = InlineData(data = base64Image)),
                    )
                )
            ),
            generationConfig = GenerationConfig(responseMimeType = "application/json"),
        )

        val httpResponse = httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestPayload)
        }

        val response: GeminiResponse = httpResponse.body()
        val rawJsonText = response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: throw IllegalStateException("Empty response from Gemini Vision API")

        return json.decodeFromString(rawJsonText)
    }
}