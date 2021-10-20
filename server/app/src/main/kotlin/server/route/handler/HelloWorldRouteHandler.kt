package server.route.handler

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.response.respondText
import server.route.RouteHandling

class HelloWorldRouteHandler : RouteHandling {

    override suspend fun handle(call: ApplicationCall) {
        return call.respondText(
            "hello, world!",
            contentType = ContentType.Text.Plain
        )
    }
}
