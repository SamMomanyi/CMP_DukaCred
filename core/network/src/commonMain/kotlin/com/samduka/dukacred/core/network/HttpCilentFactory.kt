package com.samduka.dukacred.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// expect/actual pattern — each platform provides its own engine
// but all config lives here in commonMain
expect fun createHttpClient(): HttpClient

fun createSupabaseHttpClient(
    supabaseUrl: String = SupabaseConfig.BASE_URL,
    supabaseAnonKey: String = SupabaseConfig.ANON_KEY,
): HttpClient = createHttpClient().config {

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true   // safe for API responses with extra fields
            isLenient = true           // handles unquoted keys from some APIs
            encodeDefaults = false     // don't send null fields to Supabase
        })
    }

    install(Logging) {
        logger = Logger.SIMPLE
        // Switch to LogLevel.BODY during development to see full requests
        level = LogLevel.HEADERS
    }

    defaultRequest {
        url(supabaseUrl)
        contentType(ContentType.Application.Json)

        // Supabase requires these two headers on every request
        headers.append("apikey", supabaseAnonKey)
        headers.append("Authorization", "Bearer $supabaseAnonKey")

        // Tell Supabase to return full objects on insert/update
        headers.append("Prefer", "return=representation")
    }
}