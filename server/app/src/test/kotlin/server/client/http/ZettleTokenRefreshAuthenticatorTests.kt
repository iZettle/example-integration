package server.client.http

import okhttp3.Request
import server.client.zettle.oauth.ZettleAccessTokenRefreshResponse
import server.makeResponse
import server.makeRequest
import server.session.ZettleCredentialsDTO
import java.time.Instant
import kotlin.test.*

class ZettleTokenRefreshAuthenticatorTests {

    private lateinit var mockStorer: MockZettleAccessTokenStoring
    private lateinit var mockRefresher: MockZettleAccessTokenRefreshing
    private lateinit var sut: ZettleTokenRefreshAuthenticator

    @BeforeTest
    fun setUp() {
        mockStorer = MockZettleAccessTokenStoring()
        mockRefresher = MockZettleAccessTokenRefreshing()
        sut = ZettleTokenRefreshAuthenticator(
            accessTokenStore = mockStorer,
            refresher = mockRefresher
        )
    }

    @Test
    fun `returns null if there is no Authorization header in the original request`() {
        val request = sut.authenticate(route = null, response = makeResponse())
        assertNull(request)
    }

    @Test
    fun `returns null if there is a malformed Authorization token in the original request`() {
        val originalRequest = makeRequest(authHeaderValue = "bad token")
        val response = makeResponse(request = originalRequest)
        val request = sut.authenticate(route = null, response = response)
        assertNull(request)
    }

    @Test
    fun `returns null if there has already been a refresh attempt`() {
        val response = makeResponse(request = makeRequest(), priorResponse = makeResponse(body = null))
        val request = sut.authenticate(route = null, response = response)
        assertNull(request)
    }

    @Test
    fun `returns null if there is no existing auth session`() {
        val response = makeResponse(request = makeRequest())
        val request = sut.authenticate(route = null, response = response)
        assertNull(request)
    }

    @Test
    fun `returns null if refresh fails`() {
        mockStorer.stubFetch = Result.success(
            ZettleCredentialsDTO(
                accessToken = "access token",
                accessTokenExpiresAt = Instant.MAX,
                refreshToken = "refresh token"
            )
        )
        mockRefresher.stubRefresh = Result.failure(Exception())
        val response = makeResponse(request = makeRequest())
        val request = sut.authenticate(route = null, response = response)
        assertNull(request)
    }

    @Test
    fun `returns null if new credentials fail to be stored`() {
        mockStorer.stubFetch = Result.success(
            ZettleCredentialsDTO(
                accessToken = "access token",
                accessTokenExpiresAt = Instant.MAX,
                refreshToken = "refresh token"
            )
        )
        mockRefresher.stubRefresh = Result.success(
            ZettleAccessTokenRefreshResponse(
                accessToken = "refreshed access token",
                expiresInSeconds = 1234,
                refreshToken = "refresh token",
            )
        )
        mockStorer.stubStore = Result.failure(Exception("stub exception"))
        val response = makeResponse(request = makeRequest())
        val request = sut.authenticate(route = null, response = response)
        assertNull(request)
    }

    @Test
    fun `returns correct request`() {
        mockStorer.stubFetch = Result.success(
            ZettleCredentialsDTO(
                accessToken = "access token",
                accessTokenExpiresAt = Instant.MAX,
                refreshToken = "refresh token"
            )
        )
        mockRefresher.stubRefresh = Result.success(
            ZettleAccessTokenRefreshResponse(
                accessToken = "refreshed access token",
                refreshToken = "refresh token",
                expiresInSeconds = 1234,
            )
        )
        mockStorer.stubStore = Result.success(Unit)
        var originalRequest = makeRequest()
        val response = makeResponse(request = originalRequest)
        val expected = makeRequest(authHeaderValue = "Bearer refreshed access token")
        val request = sut.authenticate(route = null, response = response)
        // todo: Un-hack test – assuming there's a better way to do this?
        assertIs<Request?>(request)
        assertNotNull(request)
        assertEquals(expected.method, request!!.method)
        assertEquals(expected.url, request!!.url)
        assertEquals(expected.headers, request!!.headers)
    }
}