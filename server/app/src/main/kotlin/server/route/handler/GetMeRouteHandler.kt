package server.route.handler

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import kotlinx.serialization.Serializable
import server.client.http.NotAuthorisedException
import server.client.http.UUIDSerializer
import server.client.zettle.user.ZettleUserFetching
import server.logger
import server.route.RouteHandling
import server.session.KtorSessionCookie
import server.session.ZettleSessionCredentialStoring
import java.util.UUID

class GetMeRouteHandler(
    private val userFetching: ZettleUserFetching,
    private val sessionStore: ZettleSessionCredentialStoring
) : RouteHandling {

    private val logger = logger<GetMeRouteHandler>()

    @Serializable
    private data class GetMeResponseBody(
        val displayName: String,
        @Serializable(with = UUIDSerializer::class) val organizationUuid: UUID
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
        val userResult = userFetching.fetchMe(accessToken).getOrElse {
            if (it is NotAuthorisedException) {
                return call.respond(HttpStatusCode.Unauthorized)
            }

            logger.error("failed to get user info", it)
            return call.respond(HttpStatusCode.InternalServerError)
        }

        // todo: find out where backoffice gets user display name from
        val displayName = userResult.uuid.toString().substring(0, 4)

        val stubbedResponseBody = GetMeResponseBody(
            displayName = displayName,
            organizationUuid = userResult.organizationUuid
        )
        return call.respond(
            HttpStatusCode.OK,
            stubbedResponseBody
        )
    }
}
