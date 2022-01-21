package server.client.zettle.oauth

import kotlinx.coroutines.runBlocking
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import server.client.http.NotAuthorisedException
import server.client.zettle.MockDecoding
import server.client.zettle.MockRequestSending
import server.makeRequest
import server.makeResponse
import kotlin.test.*

class ZettleAccessTokenRefresherTests {

    private lateinit var mockSender: MockRequestSending
    private lateinit var mockDecoder: MockDecoding
    private lateinit var sut: ZettleAccessTokenRefresher
    private val baseUrl = "https://oauth.local".toHttpUrl()
    private val clientId = "client id"
    private val clientSecret = "client secret"
    private val refreshToken = "refresh token"

    @BeforeTest
    fun setUp() {
        mockSender = MockRequestSending()
        mockDecoder = MockDecoding()
        sut = ZettleAccessTokenRefresher(
            baseUrl,
            mockSender,
            mockDecoder,
            clientId,
            clientSecret
        )
    }

    @Test
    fun `sends correct request`() = runBlocking {
        mockSender.stubSend = Result.failure(NotAuthorisedException)

        sut.refresh(refreshToken)

        val sentRequest = mockSender.spySendRequest
            ?: fail("expected a request to be sent")
        val expectedUrl = "https://oauth.local/token"
        val expectedMethod = "POST"
        assertEquals(expectedUrl, sentRequest.url.toString())
        assertEquals(expectedMethod, sentRequest.method)
        val sentBody = sentRequest.body
        assertIs<FormBody>(sentBody)
        val expectedBody = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("client_secret", "client secret")
            .add("client_id", "client id")
            .add("refresh_token", "refresh token")
            .build()
        for (i in 0 until sentBody.size) {
            assertEquals(expectedBody.name(i), sentBody.name(i))
            assertEquals(expectedBody.value(i), sentBody.value(i))
        }
    }

    @Test
    fun `successful response is returned`() = runBlocking {
        mockSender.stubSend = Result.success(
            makeResponse(code = 200, request = makeRequest())
        )
        val stubResponse = ZettleAccessTokenRefreshResponse(
            accessToken = "access token",
            refreshToken = "refresh token",
            expiresInSeconds = 1234
        )
        mockDecoder.stubDecode = stubResponse

        val result = sut.refresh(refreshToken)

        assertEquals(result.getOrNull(), stubResponse)
    }

    @Test
    fun `bad request response returns a runtime exception`() = runBlocking {
        mockSender.stubSend = Result.success(
            makeResponse(code = 456, request = makeRequest())
        )

        val result = sut.refresh("refresh token")

        assertIs<RuntimeException>(result.exceptionOrNull())
        Unit
    }

    @Test
    fun `failed send returns the failed result`() = runBlocking {
        mockSender.stubSend = Result.failure(Exception("stub exception"))

        val result = sut.refresh("refresh token")

        assertIs<Exception>(result.exceptionOrNull())
        Unit
    }

    @Test
    fun `null response body returns a runtime exception`() = runBlocking {
        mockSender.stubSend = Result.success(
            makeResponse(code = 200, body = null, request = makeRequest())
        )

        val result = sut.refresh("refresh token")

        assertIs<RuntimeException>(result.exceptionOrNull())
        Unit
    }

    @Test
    fun `malformed response returns a runtime exception`() = runBlocking {
        mockSender.stubSend = Result.success(
            makeResponse(code = 200, request = makeRequest())
        )
        val stubResponse = "invalid json"
        mockDecoder.stubDecode = stubResponse

        val result = sut.refresh("refresh token")

        assertIs<RuntimeException>(result.exceptionOrNull())
        Unit
    }
}
