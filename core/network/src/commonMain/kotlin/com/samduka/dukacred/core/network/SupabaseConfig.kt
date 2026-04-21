package com.samduka.dukacred.core.network



// These are pulled from BuildKonfig at compile time
// so no secrets ever live in source code
object SupabaseConfig {

    val BASE_URL = BuildKonfig.SUPABASE_URL

    val ANON_KEY = BuildKonfig.SUPABASE_ANON_KEY


    // Supabase REST API paths
    const val REST_PATH = "/rest/v1"
    const val AUTH_PATH = "/auth/v1"
    const val STORAGE_PATH = "/storage/v1"
    const val FUNCTIONS_PATH = "/functions/v1"
}