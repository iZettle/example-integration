package server.route.handler

import io.ktor.server.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import server.route.RouteHandling

class HelloWorldRouteHandler : RouteHandling {

    override suspend fun handle(call: ApplicationCall) {
        return call.respondText(
            "hello, world!",
            contentType = ContentType.Text.Plain
        )
    }
}
