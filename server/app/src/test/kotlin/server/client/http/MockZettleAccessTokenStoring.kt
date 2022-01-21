package server.client.http

import server.session.ZettleAccessTokenStoring
import server.session.ZettleCredentialsDTO

class MockZettleAccessTokenStoring : ZettleAccessTokenStoring {

    var stubFetch: Result<ZettleCredentialsDTO?>? = Result.success(null)
    var stubStore: Result<Unit> = Result.success(Unit)

    override fun fetchByAccessToken(accessToken: String): Result<ZettleCredentialsDTO?> {
        return stubFetch!!
    }

    override fun storeByAccessToken(accessToken: String, credentials: ZettleCredentialsDTO): Result<Unit> {
        return stubStore
    }

    override fun removeByAccessToken(accessToken: String): Result<Unit> {
        return Result.success(Unit)
    }
}