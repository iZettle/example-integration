package server

import io.ktor.http.*
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

fun makeRequest(
    url: HttpUrl = "https://test.local".toHttpUrl(),
    authHeaderValue: String? = "Bearer token"
): Request {
    val request = Request.Builder().url(url)

    if (authHeaderValue != null) request.header(HttpHeaders.Authorization, authHeaderValue)

    return request.build()
}
