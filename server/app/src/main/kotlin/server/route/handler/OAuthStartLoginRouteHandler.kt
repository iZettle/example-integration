package server.route.handler

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import okhttp3.HttpUrl
import server.logger
import server.oauth.OAuthStateStoring
import server.route.RouteHandling
import server.session.TokenGenerating

class OAuthStartLoginRouteHandler(
    private val stateTokenGenerator: TokenGenerating,
    private val oauthStateStore: OAuthStateStoring,
    private val oauthBaseUrl: HttpUrl,
    private val oauthClientId: String,
    private val oauthRedirectUri: String,
    private val oauthScope: String
) : RouteHandling {

    private val logger = logger<OAuthStartLoginRouteHandler>()

    override suspend fun handle(call: ApplicationCall) {
        val state = stateTokenGenerator.generate()
        oauthStateStore.store(state).getOrElse {
            logger.error("failed to store oauth state parameter", it)
            return call.respond(HttpStatusCode.InternalServerError)
        }

        val oauthLoginUrl = oauthBaseUrl.newBuilder()
            .addPathSegments("authorize")
            .addQueryParameter("response_type", "code")
            .addQueryParameter("client_id", oauthClientId)
            .addQueryParameter("scope", oauthScope)
            .addQueryParameter("redirect_uri", oauthRedirectUri)
            .addQueryParameter("state", state)
            .build()
            .toString()

        return call.respondRedirect(
            oauthLoginUrl,
            permanent = false
        )
    }
}
