package server.client.zettle.oauth

import kotlinx.coroutines.runBlocking
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import server.client.http.NotAuthorisedException
import server.client.zettle.MockDecoding
import server.client.zettle.MockRequestSending
import server.makeResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.fail

class ZettleCodeGrantExchangerTests {

    private lateinit var mockSender: MockRequestSending
    private lateinit var mockDecoder: MockDecoding
    private lateinit var sut: ZettleCodeGrantExchanger
    private val baseUrl = "https://oauth.local".toHttpUrl()
    private val redirectUri = "redirect uri"
    private val clientId = "client id"
    private val clientSecret = "client secret"

    @BeforeTest
    fun setUp() {
        mockSender = MockRequestSending()
        mockDecoder = MockDecoding()
        sut = ZettleCodeGrantExchanger(
            baseUrl,
            mockSender,
            mockDecoder,
            redirectUri,
            clientId,
            clientSecret
        )
    }

    @Test
    fun `sends correct request to zettle oauth`() = runBlocking {
        mockSender.stubSend = Result.failure(NotAuthorisedException)

        sut.exchange("code grant", "state")

        val sentRequest = mockSender.spySendRequest
            ?: fail("expected a sent request")
        val expectedUrl = "https://oauth.local/token"
        assertEquals(expectedUrl, sentRequest.url.toString())
        assertEquals("POST", sentRequest.method)
        val sentBody = sentRequest.body
        assertIs<FormBody>(sentBody)
        val expectedBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("redirect_uri", "redirect uri")
            .add("code", "code grant")
            .add("state", "state")
            .add("client_secret", "client secret")
            .add("client_id", "client id")
            .build()
        for (i in 0 until sentBody.size) {
            assertEquals(expectedBody.name(i), sentBody.name(i))
            assertEquals(expectedBody.value(i), sentBody.value(i))
        }
    }

    @Test
    fun `successful response from zettle auth is decoded and returned`() = runBlocking {
        mockSender.stubSend = Result.success(
            makeResponse(url = baseUrl, code = 200)
        )
        val stubResponse = ZettleCodeGrantExchangeResponse(
            accessToken = "access token",
            refreshToken = "refresh token",
            expiresInSeconds = 1234
        )
        mockDecoder.stubDecode = stubResponse

        val result = sut.exchange("code grant", "state")

        assertEquals(result.getOrNull(), stubResponse)
    }

    @Test
    fun `successful response from zettle refresh token request is decoded and returned`() = runBlocking {
        mockSender.stubSend = Result.success(
            makeResponse(url = baseUrl, code = 200)
        )
        val stubResponse = ZettleCodeGrantExchangeResponse(
            accessToken = "access token",
            refreshToken = "refresh token",
            expiresInSeconds = 1234
        )
        mockDecoder.stubDecode = stubResponse

        val result = sut.exchange("code grant", "state")
        assertEquals(result.getOrNull(), stubResponse)

        val refreshTokenStubResponse = ZettleCodeGrantExchangeResponse(
            accessToken = "access token from refresh",
            refreshToken = "new refresh token",
            expiresInSeconds = 1234
        )

        mockDecoder.stubDecode = refreshTokenStubResponse
        val resultRefreshToken = sut.exchangeRefreshToken(stubResponse.refreshToken)
        assertEquals(resultRefreshToken.getOrNull(), refreshTokenStubResponse)

    }

    @Test
    fun `an error from zettle auth returns a runtime exception`() = runBlocking {
        mockSender.stubSend = Result.success(
            makeResponse(url = baseUrl, code = 456)
        )

        val result = sut.exchange("code grant", "state")

        assertIs<RuntimeException>(result.exceptionOrNull())
        Unit
    }

    @Test
    fun `malformed response from zettle auth returns an exception`() = runBlocking {
        mockSender.stubSend = Result.success(
            makeResponse(url = baseUrl, code = 200)
        )
        val stubResponse = "invalid json"
        mockDecoder.stubDecode = stubResponse

        val result = sut.exchange("code grant", "state")

        assertIs<RuntimeException>(result.exceptionOrNull())
        Unit
    }
}
