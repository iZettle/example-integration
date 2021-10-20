package server.route.handler

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import server.client.zettle.oauth.MockZettleCodeGrantExchanging
import server.client.zettle.oauth.ZettleCodeGrantExchangeResponse
import server.oauth.MockOAuthStateStoring
import server.oauth.OAuthStateDTO
import server.runHandler
import server.session.MockTokenGenerating
import server.session.MockZettleSessionCredentialStoring
import server.utility.assertEmptyResponse
import java.time.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class OAuthGrantRedirectRouteHandlerTests {

    private lateinit var sut: OAuthGrantRedirectRouteHandler
    private lateinit var mockGrantExchanger: MockZettleCodeGrantExchanging
    private lateinit var mockCredentialStore: MockZettleSessionCredentialStoring
    private lateinit var mockTokenGenerator: MockTokenGenerating
    private lateinit var mockOAuthStateStore: MockOAuthStateStoring
    private val homeRedirectUrl = "https://test.local"

    @BeforeTest
    fun setUp() {
        mockGrantExchanger = MockZettleCodeGrantExchanging()
        mockCredentialStore = MockZettleSessionCredentialStoring()
        mockTokenGenerator = MockTokenGenerating()
        mockOAuthStateStore = MockOAuthStateStoring()
        sut = OAuthGrantRedirectRouteHandler(
            mockGrantExchanger,
            mockCredentialStore,
            mockTokenGenerator,
            homeRedirectUrl,
            mockOAuthStateStore
        )
    }

    @Test
    fun `missing code grant or state results in bad request`() {
        val cases = listOf(
            "?code=abc&state=",
            "?code=&state=123"
        )
        cases.forEach { case ->
            val call = runHandler(sut) {
                uri = case
            }

            assertEmptyResponse(call, HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun `failure to fetch oauth state results in a 500 `() {
        mockOAuthStateStore.stubFetch = Result.failure(
            RuntimeException("deliberate failure")
        )

        val call = runHandler(sut) {
            uri = "?code=abc&state=123"
        }

        assertEmptyResponse(call, HttpStatusCode.InternalServerError)
    }

    @Test
    fun `no stored state results in a 400 `() {
        mockOAuthStateStore.stubFetch = Result.success(null)

        val call = runHandler(sut) {
            uri = "?code=abc&state=123"
        }

        assertEmptyResponse(call, HttpStatusCode.BadRequest)
    }

    @Test
    fun `failed code grant exchange results in a 500`() {
        mockOAuthStateStore.stubFetch = Result.success(
            stubState()
        )
        mockGrantExchanger.stubExchange = Result.failure(
            RuntimeException("deliberate failure")
        )

        val call = runHandler(sut) {
            uri = "?code=abc&state=123"
        }

        assertEmptyResponse(call, HttpStatusCode.InternalServerError)
    }

    @Test
    fun `failure to store credentials results in a 500 `() {
        mockOAuthStateStore.stubFetch = Result.success(
            stubState()
        )
        mockGrantExchanger.stubExchange = Result.success(
            ZettleCodeGrantExchangeResponse(
                accessToken = "abc",
                refreshToken = "def",
                expiresInSeconds = 123
            )
        )
        mockCredentialStore.stubStore = Result.failure(
            RuntimeException("deliberate failure")
        )
        mockTokenGenerator.stubGenerate = "fake token"

        val call = runHandler(sut) {
            uri = "?code=abc&state=123"
        }

        assertEmptyResponse(call, HttpStatusCode.InternalServerError)
    }

    @Test
    fun `successful storage of credentials results in a redirect to home url`() {
        mockOAuthStateStore.stubFetch = Result.success(
            stubState()
        )
        val grantExchangeResponse = ZettleCodeGrantExchangeResponse(
            accessToken = "abc",
            refreshToken = "def",
            expiresInSeconds = 123
        )
        mockGrantExchanger.stubExchange = Result.success(grantExchangeResponse)
        mockCredentialStore.stubStore = Result.success(Unit)
        mockTokenGenerator.stubGenerate = "fake token"

        val call = runHandler(sut) {
            uri = "?code=abc&state=123"
        }

        assertEquals(mockCredentialStore.spyStoreSessionToken, "fake token")
        assertEmptyResponse(call, HttpStatusCode.Found)
        assertEquals(homeRedirectUrl, call.response.headers[HttpHeaders.Location])
    }

    private fun stubState(): OAuthStateDTO {
        return OAuthStateDTO(
            state = "test state",
            createdAt = Instant.now()
        )
    }
}
