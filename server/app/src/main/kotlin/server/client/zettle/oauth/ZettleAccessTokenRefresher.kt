package server.client.zettle.oauth

import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.Request
import server.client.http.Decoding
import server.client.http.HttpRequestSending
import server.client.http.decode

class ZettleAccessTokenRefresher(
    private val baseUrl: HttpUrl,
    private val requestSending: HttpRequestSending,
    private val decoder: Decoding,
    private val clientId: String,
    private val clientSecret: String
) : ZettleAccessTokenRefreshing {

    override suspend fun refresh(
        refreshToken: String
    ): Result<ZettleAccessTokenRefreshResponse> {
        val url = baseUrl.newBuilder()
            .addPathSegments("token")
            .build()
        val requestBody = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("client_secret", clientSecret)
            .add("client_id", clientId)
            .add("refresh_token", refreshToken)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = requestSending.send(request).getOrElse {
            return Result.failure(it)
        }
        if (!response.isSuccessful) {
            return Result.failure(RuntimeException("unexpected response code: ${response.code}"))
        }

        val responseBody = response.body?.string()
            ?: return Result.failure(RuntimeException("expected a response body"))

        return decoder.decode(responseBody)
    }
}
