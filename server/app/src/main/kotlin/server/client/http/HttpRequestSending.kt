package server.client.http

import okhttp3.Request
import okhttp3.Response

interface HttpRequestSending {

    suspend fun send(request: Request): Result<Response>
}
