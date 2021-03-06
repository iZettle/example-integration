package server.client.zettle.oauth

interface ZettleCodeGrantExchanging {

    suspend fun exchange(
        codeGrant: String,
        state: String
    ): Result<ZettleCodeGrantExchangeResponse>

    suspend fun exchangeRefreshToken(
        refreshToken: String
    ): Result<ZettleCodeGrantExchangeResponse>

}
