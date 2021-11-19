package server.route.handler

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import kotlinx.serialization.Serializable
import server.client.http.NotAuthorisedException
import server.client.http.UUIDSerializer
import server.client.zettle.product.ZettleProductFetching
import server.logger
import server.route.RouteHandling
import server.session.KtorSessionCookie
import server.session.ZettleSessionCredentialStoring
import java.util.UUID

class GetProductRouteHandler(
    private val productFetching: ZettleProductFetching,
    private val sessionStore: ZettleSessionCredentialStoring
) : RouteHandling {

    private val logger = logger<GetProductRouteHandler>()

    @Serializable
    private data class GetProductResponseBody(
        val productCount: Integer
    )

    override suspend fun handle(call: ApplicationCall) {
        val session = call.sessions.get<KtorSessionCookie>()
            ?: return call.respond(HttpStatusCode.Unauthorized)
        val accessToken = sessionStore
            .fetch(session.sessionToken)
            .getOrNull()
            ?.accessToken
        if (accessToken == null) {
            logger.warn("no zettle session found for user session")
            return call.respond(HttpStatusCode.Unauthorized)
        }
        val productResult = productFetching.fetchProduct(accessToken).getOrElse {
            if (it is NotAuthorisedException) {
                return call.respond(HttpStatusCode.Unauthorized)
            }

            logger.error("failed to get product info", it)
            return call.respond(HttpStatusCode.InternalServerError)
        }

        val stubbedResponseBody = GetProductResponseBody(
            productCount = productResult.productCount
        )
        return call.respond(
            HttpStatusCode.OK,
            stubbedResponseBody
        )
    }
}
