package server.route.handler

import server.client.zettle.user.ZettleUserFetching
import server.client.zettle.user.ZettleUserResponse

class MockZettleUserFetching : ZettleUserFetching {

    var stubFetchMe: Result<ZettleUserResponse>? = null

    override suspend fun fetchMe(accessToken: String): Result<ZettleUserResponse> {
        return stubFetchMe!!
    }
}
