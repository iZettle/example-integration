package server.session

interface ZettleSessionCredentialStoring {

    fun fetch(
        sessionToken: String
    ): Result<ZettleCredentialsDTO?>

    fun store(
        sessionToken: String,
        credentials: ZettleCredentialsDTO
    ): Result<Unit>

    fun remove(sessionToken: String): Result<Unit>
}
