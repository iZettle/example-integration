package server.route.handler

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import okhttp3.HttpUrl.Companion.toHttpUrl
import server.oauth.MockOAuthStateStoring
import server.runHandler
import server.session.MockTokenGenerating
import server.utility.assertEmptyResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class OAuthStartLoginHandlerTests {

    private lateinit var mockStateTokenGenerator: MockTokenGenerating
    private lateinit var mockOAuthStateStore: MockOAuthStateStoring
    private lateinit var sut: OAuthStartLoginRouteHandler
    private val oauthBaseUrl = "http://test.local".toHttpUrl()
    private val oauthClientId = "test_client_id"
    private val oauthRedirectUri = "http://redirect.local"
    private val oauthScope = "TEST:TEST"

    @BeforeTest
    fun setUp() {
        mockStateTokenGenerator = MockTokenGenerating()
        mockStateTokenGenerator.stubGenerate = "test token"
        mockOAuthStateStore = MockOAuthStateStoring()
        sut = OAuthStartLoginRouteHandler(
            mockStateTokenGenerator,
            mockOAuthStateStore,
            oauthBaseUrl,
            oauthClientId,
            oauthRedirectUri,
            oauthScope
        )
    }

    @Test
    fun `failure to store state results in a 500`() {
        mockOAuthStateStore.stubStore = Result.failure(
            RuntimeException("intentional failure")
        )

        val call = runHandler(sut)

        assertEmptyResponse(call, HttpStatusCode.InternalServerError)
    }

    @Test
    fun `redirects to correct url`() {
        mockOAuthStateStore.stubStore = Result.success(Unit)

        val call = runHandler(sut)

        val expectedRedirect = "http://test.local/authorize?response_type=code&client_id=test_client_id&scope=TEST%3ATEST&redirect_uri=http%3A%2F%2Fredirect.local&state=test%20token"
        assertEmptyResponse(call, HttpStatusCode.Found)
        assertEquals(expectedRedirect, call.response.headers[HttpHeaders.Location])
    }
}
