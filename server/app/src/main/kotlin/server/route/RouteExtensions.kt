package server.route

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
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
