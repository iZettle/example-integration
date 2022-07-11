package server.client.zettle.oauth

import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.Request
import server.client.http.Decoding
import server.client.http.HttpRequestSending
import server.client.http.decode
import server.logger

class ZettleCodeGrantExchanger(
    private val baseUrl: HttpUrl,
    private val requestSending: HttpRequestSending,
    private val decoder: Decoding,
    private val redirectUri: String,
    private val clientId: String,
    private val clientSecret: String
) : ZettleCodeGrantExchanging {

    override suspend fun exchange(
        codeGrant: String,
        state: String
    ): Result<ZettleCodeGrantExchangeResponse> {
        val url = baseUrl.newBuilder()
            .addPathSegments("token")
            .build()
        val requestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("redirect_uri", redirectUri)
            .add("code", codeGrant)
            .add("state", state)
            .add("client_secret", clientSecret)
            .add("client_id", clientId)
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

        @Suppress("BlockingMethodInNonBlockingContext")
        val responseBody = response.body?.string()
            ?: return Result.failure(RuntimeException("expected a response body"))

        return decoder.decode(responseBody)
    }

    override suspend fun exchangeRefreshToken(
        refreshToken: String
    ): Result<ZettleCodeGrantExchangeResponse> {
        val url = baseUrl.newBuilder()
            .addPathSegments("token")
            .build()
        val requestBody = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", refreshToken)
            .add("client_secret", clientSecret)
            .add("client_id", clientId)
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

        @Suppress("BlockingMethodInNonBlockingContext")
        val responseBody = response.body?.string()
            ?: return Result.failure(RuntimeException("expected a response body"))

        return decoder.decode(responseBody)

    }
}
