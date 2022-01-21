package server.client.http

import io.ktor.http.HttpHeaders
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import server.client.zettle.oauth.ZettleAccessTokenRefreshing
import server.logger
import server.session.ZettleAccessTokenStoring
import server.session.ZettleCredentialsDTO
import java.time.Clock
import java.time.Instant

class ZettleTokenRefreshAuthenticator(
    private val accessTokenStore: ZettleAccessTokenStoring,
    private val refresher: ZettleAccessTokenRefreshing
) : Authenticator {

    private val logger = logger<ZettleTokenRefreshAuthenticator>()

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {
        val originalRequest = response.request
        val originalAccessToken = extractAccessToken(originalRequest)
            ?: return null

        if (hasAlreadyTriedToAuth(response)) {
            logger.warn("already tried to refresh token once, unsuccessfully - bailing out")
            return null
        }

        synchronized(this) {
            // todo: fix threading race condition - don't refresh twice

            val refreshToken = accessTokenStore
                .fetchByAccessToken(originalAccessToken)
                .getOrNull()
                ?.refreshToken
            if (refreshToken == null) {
                logger.warn("couldn't find existing auth session for outgoing request, that requires a token refresh")
                return null
            }
            val result = runBlocking {
                refresher.refresh(refreshToken)
            }
            val newSession = result.getOrElse {
                logger.warn("failed to refresh auth token for request", it)
                return null
            }
            val expiresAt = Instant.now(Clock.systemUTC())
                .plusSeconds(newSession.expiresInSeconds.toLong())
            val credentials = ZettleCredentialsDTO(
                accessToken = newSession.accessToken,
                refreshToken = newSession.refreshToken,
                accessTokenExpiresAt = expiresAt
            )
            // todo: Is this correct? Not storing by the original access token?
            val storeResult = accessTokenStore.storeByAccessToken(
                accessToken = newSession.accessToken,
                credentials = credentials
            )
            if (storeResult.isFailure) {
                logger.warn("failed to store new credentials", storeResult.exceptionOrNull())
                return null
            }

            // todo: Note change to replace header – now less explicit, though?
            return originalRequest.newBuilder()
                .header(HttpHeaders.Authorization, "Bearer ${credentials.accessToken}")
                .build()
        }
    }

    private fun extractAccessToken(
        request: Request
    ): String? {
        val authHeader = request.header(HttpHeaders.Authorization)
            ?: return null

        val bearerPrefix = "Bearer "
        if (!authHeader.startsWith(bearerPrefix)) {
            return null
        }
        return authHeader
            .removePrefix(bearerPrefix)
            .trim()
    }

    private fun hasAlreadyTriedToAuth(response: Response): Boolean {
        if (response.priorResponse != null) {
            return true
        }

        return false
    }
}
