package server.client.zettle.user

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import okhttp3.HttpUrl
import okhttp3.Request
import server.client.http.Decoding
import server.client.http.HttpRequestSending
import server.client.http.NotAuthorisedException
import server.client.http.decode

class ZettleUserFetcher(
    private val baseUrl: HttpUrl,
    private val requestSending: HttpRequestSending,
    private val decoder: Decoding
) : ZettleUserFetching {

    override suspend fun fetchMe(accessToken: String): Result<ZettleUserResponse> {
        val url = baseUrl.newBuilder()
            .addPathSegments("users/me")
            .build()
        val request = Request.Builder()
            .get()
            .header(HttpHeaders.Authorization, "Bearer $accessToken")
            .url(url)
            .build()

        val response = requestSending.send(request).getOrElse {
            return Result.failure(it)
        }
        if (!response.isSuccessful) {
            if (response.code == HttpStatusCode.Unauthorized.value) {
                return Result.failure(NotAuthorisedException)
            }

            return Result.failure(RuntimeException("unexpected response code: ${response.code}"))
        }
        @Suppress("BlockingMethodInNonBlockingContext")
        val responseBody = response.body?.string()
            ?: return Result.failure(RuntimeException("expected a response body"))

        return decoder.decode(responseBody)
    }
}
