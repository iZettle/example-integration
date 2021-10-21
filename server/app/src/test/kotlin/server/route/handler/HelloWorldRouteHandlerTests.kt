package server.route.handler

import io.ktor.http.HttpHeaders
import org.junit.Test
import server.runHandler
import kotlin.test.assertEquals
import kotlin.test.fail

class HelloWorldRouteHandlerTests {

    @Test
    fun `handler responds with hello world`() {
        val handler = HelloWorldRouteHandler()

        val call = runHandler(handler)

        val responseContent = call.response.content ?: fail("expected a response body")
        val expectedContentType = "text/plain; charset=UTF-8"
        assertEquals(expectedContentType, call.response.headers[HttpHeaders.ContentType])
        assertEquals("hello, world!", responseContent)
    }
}
