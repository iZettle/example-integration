package server.client.http

import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import server.logger
import java.time.Duration

class OkHttpRequestSender : HttpRequestSending {

    private val logger = logger<OkHttpRequestSender>()
    private val defaultTimeout = Duration.ofSeconds(10)
    private val client = OkHttpClient.Builder()
        .callTimeout(defaultTimeout)
        .readTimeout(defaultTimeout)
        .writeTimeout(defaultTimeout)
        .cookieJar(CookieJar.NO_COOKIES)
        .cache(null)
        .build()

    override suspend fun send(request: Request): Result<Response> {
        return runCatching {
            logger.info("sent a request: ${request.method} ${request.url}")
            client.newCall(request).await()
        }
    }
}
