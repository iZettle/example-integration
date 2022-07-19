package server.route

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.util.pipeline.PipelineInterceptor

fun Route.get(
    path: String,
    routeHandler: RouteHandling
): Route {
    val interceptor = makeInterceptor(routeHandler)
    return get(path, interceptor)
}

private fun makeInterceptor(
    routeHandler: RouteHandling
): PipelineInterceptor<Unit, ApplicationCall> {
    return {
        routeHandler.handle(call)
    }
}
