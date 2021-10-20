package server.client.zettle.user

interface ZettleUserFetching {

    suspend fun fetchMe(accessToken: String): Result<ZettleUserResponse>
}
