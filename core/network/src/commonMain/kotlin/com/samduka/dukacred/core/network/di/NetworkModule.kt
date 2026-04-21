package com.samduka.dukacred.core.network.di


import com.samduka.dukacred.core.network.BuildKonfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import org.koin.dsl.module

val networkModule = module {

    // One SupabaseClient instance for the entire app
    // The SDK handles auth token refresh, retries, and serialization
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = BuildKonfig.SUPABASE_URL,
            supabaseKey = BuildKonfig.SUPABASE_ANON_KEY,
        ) {
            install(Auth)         // handles sign in, sign up, session refresh
            install(Postgrest)    // handles all database queries
            install(Storage)      // handles invoice image uploads
            install(Functions)    // handles edge functions for AI extraction
        }
    }
}