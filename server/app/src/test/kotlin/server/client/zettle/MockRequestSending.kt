package server.client.zettle

import okhttp3.Request
import okhttp3.Response
import server.client.http.HttpRequestSending

class MockRequestSending : HttpRequestSending {

    var spySendRequest: Request? = null
    var stubSend: Result<Response>? = null

    override suspend fun send(request: Request): Result<Response> {
        spySendRequest = request
        return stubSend!!
    }
}
