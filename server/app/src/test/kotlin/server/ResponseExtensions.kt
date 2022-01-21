package server

import io.ktor.http.*
import io.ktor.http.auth.*
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

fun makeResponse(
    code: Int = 200,
    body: ResponseBody? = "".toResponseBody(contentType = "application/json".toMediaType()),
    request: Request = Request.Builder().url("https://test.local".toHttpUrl()).build(),
    priorResponse: Response? = null
): Response {
    return Response.Builder()
        .request(request)
        .priorResponse(priorResponse)
        .protocol(Protocol.HTTP_1_0)
        .message("")
        .code(code)
        .body(body)
        .build()
}
