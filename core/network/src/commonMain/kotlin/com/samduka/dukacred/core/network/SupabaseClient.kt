package com.samduka.dukacred.core.network

import com.samduka.dukacred.core.common.error.AppError
import com.samduka.dukacred.core.common.error.AuthError
import com.samduka.dukacred.core.common.error.NetworkError
import com.samduka.dukacred.core.common.error.StorageError
import com.samduka.dukacred.core.common.result.AppResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.json.JsonObject

class SupabaseClient(
    private val httpClient: HttpClient,
    private val baseUrl: String = SupabaseConfig.BASE_URL,
) {

    // ── Auth ──────────────────────────────────────────────────────────────

    suspend fun signInWithPassword(
        email: String,
        password: String,
    ): AppResult<AuthResponse, AuthError> = safeCall {
        httpClient.post("$baseUrl${SupabaseConfig.AUTH_PATH}/token?grant_type=password") {
            setBody(mapOf("email" to email, "password" to password))
        }.body<AuthResponse>()
    }

    suspend fun signInWithPhone(
        phone: String,
        password: String,
    ): AppResult<AuthResponse, AuthError> = safeCall {
        httpClient.post("$baseUrl${SupabaseConfig.AUTH_PATH}/token?grant_type=password") {
            setBody(mapOf("phone" to phone, "password" to password))
        }.body<AuthResponse>()
    }

    suspend fun signUp(
        email: String,
        password: String,
        metadata: Map<String, String> = emptyMap(),
    ): AppResult<AuthResponse, AuthError> = safeCall {
        httpClient.post("$baseUrl${SupabaseConfig.AUTH_PATH}/signup") {
            setBody(mapOf(
                "email" to email,
                "password" to password,
                "data" to metadata,
            ))
        }.body<AuthResponse>()
    }

    suspend fun signOut(accessToken: String): AppResult<Unit, AuthError> = safeCall {
        httpClient.post("$baseUrl${SupabaseConfig.AUTH_PATH}/logout") {
            header("Authorization", "Bearer $accessToken")
        }
        Unit
    }

    // ── Database ──────────────────────────────────────────────────────────

    suspend inline fun <reified T> getAll(
        table: String,
        accessToken: String? = null,
        filters: Map<String, String> = emptyMap(),
        orderBy: String? = null,
        limit: Int? = null,
    ): AppResult<List<T>, NetworkError> = safeCall {
        httpClient.get("$baseUrl${SupabaseConfig.REST_PATH}/$table") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            filters.forEach { (key, value) ->
                url.parameters.append(key, value)
            }
            orderBy?.let { url.parameters.append("order", it) }
            limit?.let { url.parameters.append("limit", it.toString()) }
        }.body<List<T>>()
    }

    suspend inline fun <reified T> getOne(
        table: String,
        filter: String,
        filterValue: String,
        accessToken: String? = null,
    ): AppResult<T?, NetworkError> = safeCall {
        val results = httpClient.get("$baseUrl${SupabaseConfig.REST_PATH}/$table") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            url.parameters.append(filter, "eq.$filterValue")
            // Tell Supabase we expect one row
            headers.append("Accept", "application/vnd.pgrst.object+json")
        }.body<List<T>>()
        results.firstOrNull()
    }

    suspend inline fun <reified T> insert(
        table: String,
        body: T,
        accessToken: String? = null,
    ): AppResult<T, NetworkError> = safeCall {
        httpClient.post("$baseUrl${SupabaseConfig.REST_PATH}/$table") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            setBody(body)
        }.body<T>()
    }

    suspend inline fun <reified T> update(
        table: String,
        filter: String,
        filterValue: String,
        body: T,
        accessToken: String? = null,
    ): AppResult<T, NetworkError> = safeCall {
        httpClient.patch("$baseUrl${SupabaseConfig.REST_PATH}/$table") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            url.parameters.append(filter, "eq.$filterValue")
            setBody(body)
        }.body<T>()
    }

    suspend fun delete(
        table: String,
        filter: String,
        filterValue: String,
        accessToken: String? = null,
    ): AppResult<Unit, NetworkError> = safeCall {
        httpClient.delete("$baseUrl${SupabaseConfig.REST_PATH}/$table") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            url.parameters.append(filter, "eq.$filterValue")
        }
        Unit
    }

    // ── Edge Functions (AI extraction will call this) ─────────────────────

    suspend inline fun <reified Req, reified Res> invokeFunction(
        functionName: String,
        body: Req,
        accessToken: String? = null,
    ): AppResult<Res, NetworkError> = safeCall {
        httpClient.post("$baseUrl${SupabaseConfig.FUNCTIONS_PATH}/$functionName") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            setBody(body)
        }.body<Res>()
    }

    // ── Safe call wrapper ─────────────────────────────────────────────────
    // Catches network exceptions and maps them to typed AppResult.Failure
    // so no try/catch needed in repositories

    @Suppress("UNCHECKED_CAST")
    suspend fun <T, E : AppError> safeCall(
        block: suspend () -> T,
    ): AppResult<T, E> = try {
        AppResult.Success(block())
    } catch (e: io.ktor.client.plugins.ClientRequestException) {
        val message = when (e.response.status) {
            HttpStatusCode.Unauthorized -> "Session expired. Please sign in again."
            HttpStatusCode.Forbidden    -> "You don't have permission to do that."
            HttpStatusCode.NotFound     -> "The requested resource was not found."
            HttpStatusCode.Conflict     -> "A duplicate record already exists."
            else -> "Request failed: ${e.response.status.description}"
        }
        AppResult.Failure(NetworkError(message = message, isRetryable = false) as E)
    } catch (e: io.ktor.client.plugins.ServerResponseException) {
        AppResult.Failure(
            NetworkError(
                message = "Server error. Please try again.",
                isRetryable = true,
            ) as E
        )
    } catch (e: Exception) {
        AppResult.Failure(
            NetworkError(
                message = e.message ?: "An unexpected error occurred.",
                isRetryable = true,
            ) as E
        )
    }
}