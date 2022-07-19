package server

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import server.route.RouteHandling
import server.session.KtorSessionCookie

fun runHandler(
    handler: RouteHandling,
    sessionCookie: KtorSessionCookie? = null,
    callSetup: TestApplicationRequest.() -> Unit = {},
): TestApplicationCall {
    return withTestApplication(Application::build) {
        this.application
            .routing {
                get("/") {
                    if (sessionCookie != null) {
                        call.sessions.set(sessionCookie)
                    }
                    handler.handle(call)
                }
            }
        val call = handleRequest(setup = callSetup)
        return@withTestApplication call
    }
}
