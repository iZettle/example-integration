package server.client.zettle.user

import io.ktor.http.HttpHeaders
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrl
import server.client.http.NotAuthorisedException
import server.client.zettle.MockDecoding
import server.client.zettle.MockRequestSending
import server.extension.uuidOne
import server.extension.uuidTwo
import server.makeResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.fail

class ZettleUserFetcherTests {

    private lateinit var mockSender: MockRequestSending
    private lateinit var mockDecoder: MockDecoding
    private lateinit var sut: ZettleUserFetcher
    private val baseUrl = "https://test.local".toHttpUrl()

    @BeforeTest
    fun setUp() {
        mockSender = MockRequestSending()
        mockDecoder = MockDecoding()
        sut = ZettleUserFetcher(baseUrl, mockSender, mockDecoder)
    }

    @Test
    fun `fetcher sends correct request to zettle oauth`() = runBlocking {
        mockSender.stubSend = Result.failure(NotAuthorisedException)
        val authToken = "test token"

        sut.fetchMe(authToken)

        val sentRequest = mockSender.spySendRequest
            ?: fail("expected a sent request")
        val expectedUrl = "https://test.local/users/self"
        assertEquals(expectedUrl, sentRequest.url.toString())
        assertEquals("GET", sentRequest.method)
        assertEquals("Bearer test token", sentRequest.header(HttpHeaders.Authorization))
    }

    @Test
    fun `successful response from zettle auth is decoded and returned`() = runBlocking {
        mockSender.stubSend = Result.success(
            makeResponse(url = baseUrl, code = 200)
        )
        val stubResponse = ZettleUserResponse(
            uuid = uuidOne(),
            organizationUuid = uuidTwo()
        )
        mockDecoder.stubDecode = stubResponse

        val result = sut.fetchMe("")

        assertEquals(result.getOrNull(), stubResponse)
    }

    @Test
    fun `a 401 from zettle auth results in NotAuthorisedException`() = runBlocking {
        mockSender.stubSend = Result.success(
            makeResponse(url = baseUrl, code = 401)
        )

        val result = sut.fetchMe("")

        assertEquals(result.exceptionOrNull(), NotAuthorisedException)
    }

    @Test
    fun `any other error from zettle auth returns a runtime exception`() = runBlocking {
        mockSender.stubSend = Result.success(
            makeResponse(url = baseUrl, code = 456)
        )

        val result = sut.fetchMe("")

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

        val result = sut.fetchMe("")

        assertIs<RuntimeException>(result.exceptionOrNull())
        Unit
    }
}
