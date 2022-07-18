package server.route.handler

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import server.client.http.NotAuthorisedException
import server.client.http.UUIDSerializer
import server.client.zettle.user.ZettleUserResponse
import server.extension.uuidOne
import server.extension.uuidTwo
import server.runHandler
import server.session.KtorSessionCookie
import server.session.MockZettleSessionCredentialStoring
import server.session.ZettleCredentialsDTO
import java.time.Instant
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

class GetMeRouteHandlerTests {

    @Serializable
    private data class ExpectedResponseBody(
        @Serializable(with = UUIDSerializer::class) val uuid: UUID,
        @Serializable(with = UUIDSerializer::class) val organizationUuid: UUID
    )

    private lateinit var mockUserFetcher: MockZettleUserFetching
    private lateinit var mockSessionStore: MockZettleSessionCredentialStoring
    private lateinit var sut: GetMeRouteHandler

    @BeforeTest
    fun setUp() {
        mockUserFetcher = MockZettleUserFetching()
        mockSessionStore = MockZettleSessionCredentialStoring()

        sut = GetMeRouteHandler(mockUserFetcher, mockSessionStore)
    }

    @Test
    fun `successful zettle oauth response results in 200 with correct body`() = runBlocking {
        mockUserFetcher.stubFetchMe = Result.success(
            ZettleUserResponse(
                uuid = uuidOne(),
                organizationUuid = uuidTwo()
            )
        )
        val sessionCookie = KtorSessionCookie("")
        mockSessionStore.stubGet = Result.success(makeZettleCredentials())

        val call = runHandler(sut, sessionCookie)

        val responseContent = call.response.content ?: fail("expected a response body")
        val responseBody = Json.decodeFromString<ExpectedResponseBody>(responseContent)
        val expectedResponseBody = ExpectedResponseBody(
            uuid = uuidOne(),
            organizationUuid = uuidTwo()
        )
        assertEquals(HttpStatusCode.OK, call.response.status())
        assertEquals("application/json; charset=UTF-8", call.response.headers[HttpHeaders.ContentType])
        assertEquals(expectedResponseBody, responseBody)
    }

    @Test
    fun `no session cookie results in 401`() {
        mockUserFetcher.stubFetchMe = Result.failure(NotAuthorisedException)
        mockSessionStore.stubGet = Result.success(null)
        val sessionCookie = KtorSessionCookie("")

        val call = runHandler(sut, sessionCookie)

        val responseContent = call.response.content
        assertEquals(HttpStatusCode.Unauthorized, call.response.status())
        assertNull(call.response.headers[HttpHeaders.ContentType])
        assertNull(responseContent)
    }

    @Test
    fun `no credentials for session cookie results in 401`() {
        mockUserFetcher.stubFetchMe = Result.failure(NotAuthorisedException)
        mockSessionStore.stubGet = Result.success(null)

        val call = runHandler(sut)

        val responseContent = call.response.content
        assertEquals(HttpStatusCode.Unauthorized, call.response.status())
        assertNull(call.response.headers[HttpHeaders.ContentType])
        assertNull(responseContent)
    }

    @Test
    fun `failed zettle oauth response, because of authorisation, results in a 401`() = runBlocking {
        mockUserFetcher.stubFetchMe = Result.failure(NotAuthorisedException)
        val sessionCookie = KtorSessionCookie("")
        mockSessionStore.stubGet = Result.success(makeZettleCredentials())

        val call = runHandler(sut, sessionCookie)

        val responseContent = call.response.content
        assertEquals(HttpStatusCode.Unauthorized, call.response.status())
        assertNull(call.response.headers[HttpHeaders.ContentType])
        assertNull(responseContent)
    }

    @Test
    fun `failed zettle oauth response, unrelated to authorisation, results in a 500`() = runBlocking {
        mockUserFetcher.stubFetchMe = Result.failure(RuntimeException("fake error"))
        val sessionCookie = KtorSessionCookie("")
        mockSessionStore.stubGet = Result.success(makeZettleCredentials())

        val call = runHandler(sut, sessionCookie)

        val responseContent = call.response.content
        assertEquals(HttpStatusCode.InternalServerError, call.response.status())
        assertNull(call.response.headers[HttpHeaders.ContentType])
        assertNull(responseContent)
    }

    private fun makeZettleCredentials(): ZettleCredentialsDTO {
        return ZettleCredentialsDTO(
            accessToken = "access token",
            accessTokenExpiresAt = Instant.now().plusSeconds(60),
            refreshToken = "refresh token"
        )
    }
}
