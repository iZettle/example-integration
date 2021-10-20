package server.route.handler

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import server.client.zettle.oauth.ZettleCodeGrantExchanging
import server.logger
import server.oauth.OAuthStateStoring
import server.route.RouteHandling
import server.session.KtorSessionCookie
import server.session.TokenGenerating
import server.session.ZettleCredentialsDTO
import server.session.ZettleSessionCredentialStoring
import java.time.Clock
import java.time.Instant

class OAuthGrantRedirectRouteHandler(
    private val grantExchanger: ZettleCodeGrantExchanging,
    private val credentialStore: ZettleSessionCredentialStoring,
    private val tokenGenerator: TokenGenerating,
    private val homeRedirectUrl: String,
    private val oauthStateStore: OAuthStateStoring
) : RouteHandling {

    private val logger = logger<OAuthGrantRedirectRouteHandler>()

    override suspend fun handle(call: ApplicationCall) {
        val stateQueryParameter = call.request.queryParameters["state"]
        val codeGrant = call.request.queryParameters["code"]

        if (stateQueryParameter.isNullOrBlank()) {
            return call.respond(HttpStatusCode.BadRequest)
        }
        if (codeGrant.isNullOrBlank()) {
            return call.respond(HttpStatusCode.BadRequest)
        }

        val storedState = oauthStateStore.fetch(stateQueryParameter)
            .getOrElse {
                logger.warn("failed to load stored oauth state details", it)
                return call.respond(HttpStatusCode.InternalServerError)
            }
        val state = storedState?.state
        if (state == null) {
            logger.warn("redirect handled but we didn't recognise the provided state")
            return call.respond(HttpStatusCode.BadRequest)
        }

        val exchangeResponse = grantExchanger.exchange(
            codeGrant = codeGrant,
            state = state
        ).getOrElse {
            logger.error("failed to exchange code grant", it)
            return call.respond(HttpStatusCode.InternalServerError)
        }

        val expiresAt = Instant.now(Clock.systemUTC())
            .plusSeconds(exchangeResponse.expiresInSeconds.toLong())
        val sessionToken = tokenGenerator.generate()
        val sessionCookie = KtorSessionCookie(
            sessionToken = sessionToken
        )
        val credentials = ZettleCredentialsDTO(
            accessToken = exchangeResponse.accessToken,
            refreshToken = exchangeResponse.refreshToken,
            accessTokenExpiresAt = expiresAt
        )

        credentialStore.store(sessionToken, credentials).getOrElse {
            logger.error("failed to store credentials", it)
            return call.respond(HttpStatusCode.InternalServerError)
        }
        call.sessions.set(sessionCookie)

        return call.respondRedirect(homeRedirectUrl)
    }
}
