package server.client.zettle.oauth

interface ZettleAccessTokenRefreshing {

    suspend fun refresh(
        refreshToken: String
    ): Result<ZettleAccessTokenRefreshResponse>
}
