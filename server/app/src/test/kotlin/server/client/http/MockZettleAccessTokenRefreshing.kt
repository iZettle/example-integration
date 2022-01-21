package server.client.http

import server.client.zettle.oauth.ZettleAccessTokenRefreshResponse
import server.client.zettle.oauth.ZettleAccessTokenRefreshing

class MockZettleAccessTokenRefreshing : ZettleAccessTokenRefreshing {

    var stubRefresh: Result<ZettleAccessTokenRefreshResponse>? = null

    override suspend fun refresh(refreshToken: String): Result<ZettleAccessTokenRefreshResponse> {
        return stubRefresh!!
    }
}
