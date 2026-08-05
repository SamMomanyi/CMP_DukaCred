package com.samduka.dukacred.core.network.di

import com.samduka.dukacred.core.network.BuildKonfig
import com.samduka.dukacred.core.network.api.GeminiInvoiceApi
import com.samduka.dukacred.core.network.createHttpClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
    }

    // Ktor HttpClient instance for generic REST endpoints (like Gemini)
    single {
        createHttpClient().config {
            install(ContentNegotiation) {
                json(get())
            }
        }
    }

    // Gemini API singleton
    single { GeminiInvoiceApi(httpClient = get(), json = get()) }

    // Supabase Client
    single {
        createSupabaseClient(
            supabaseUrl = BuildKonfig.SUPABASE_URL,
            supabaseKey = BuildKonfig.SUPABASE_ANON_KEY,
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
            install(Functions)
        }
    }
}