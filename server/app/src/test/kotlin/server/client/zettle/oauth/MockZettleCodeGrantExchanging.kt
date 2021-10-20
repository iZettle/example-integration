package server.client.zettle.oauth

class MockZettleCodeGrantExchanging : ZettleCodeGrantExchanging {

    var stubExchange: Result<ZettleCodeGrantExchangeResponse>? = null

    override suspend fun exchange(codeGrant: String, state: String): Result<ZettleCodeGrantExchangeResponse> {
        return stubExchange!!
    }
}
