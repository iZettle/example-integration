package server.route

import io.ktor.server.application.ApplicationCall

interface RouteHandling {

    suspend fun handle(call: ApplicationCall)
}
