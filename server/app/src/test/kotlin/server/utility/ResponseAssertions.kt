package server.utility

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationCall
import kotlin.test.assertEquals
import kotlin.test.assertNull

fun assertEmptyResponse(
    call: TestApplicationCall,
    code: HttpStatusCode
) {
    val responseContent = call.response.content
    assertEquals(code, call.response.status())
    assertNull(call.response.headers[HttpHeaders.ContentType])
    assertNull(responseContent)
}
