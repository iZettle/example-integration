package server.session

interface ZettleAccessTokenStoring {

    fun fetchByAccessToken(
        accessToken: String
    ): Result<ZettleCredentialsDTO?>

    fun storeByAccessToken(
        accessToken: String,
        credentials: ZettleCredentialsDTO
    ): Result<Unit>

    fun removeByAccessToken(accessToken: String): Result<Unit>
}
