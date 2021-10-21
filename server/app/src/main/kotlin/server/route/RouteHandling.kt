package server.route

import io.ktor.application.ApplicationCall

interface RouteHandling {

    suspend fun handle(call: ApplicationCall)
}
