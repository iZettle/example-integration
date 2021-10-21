package server

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

fun makeResponse(
    url: HttpUrl = "https://test.local".toHttpUrl(),
    code: Int = 200,
    body: ResponseBody? = "".toResponseBody(contentType = "application/json".toMediaType())
): Response {
    return Response.Builder()
        .request(Request.Builder().url(url).build())
        .protocol(Protocol.HTTP_1_0)
        .message("")
        .code(code)
        .body(body)
        .build()
}
