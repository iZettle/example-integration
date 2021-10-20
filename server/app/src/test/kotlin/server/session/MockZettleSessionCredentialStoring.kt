package server.session

class MockZettleSessionCredentialStoring : ZettleSessionCredentialStoring {

    var stubGet: Result<ZettleCredentialsDTO?>? = null
    var stubStore: Result<Unit>? = null
    var spyStoreSessionToken: String? = null
    var spyStoreZettleCredentials: ZettleCredentialsDTO? = null
    var stubRemove: Result<Unit>? = null

    override fun fetch(sessionToken: String): Result<ZettleCredentialsDTO?> {
        return stubGet!!
    }

    override fun store(sessionToken: String, credentials: ZettleCredentialsDTO): Result<Unit> {
        spyStoreSessionToken = sessionToken
        spyStoreZettleCredentials = credentials
        return stubStore!!
    }

    override fun remove(sessionToken: String): Result<Unit> {
        return stubRemove!!
    }
}
