package server.session

import java.util.concurrent.ConcurrentHashMap

class InMemoryZettleSessionCredentialStore : ZettleSessionCredentialStoring {

    private val storage = ConcurrentHashMap<String, ZettleCredentialsDTO>()

    override fun fetch(sessionToken: String): Result<ZettleCredentialsDTO?> {
        return runCatching {
            storage[sessionToken]
        }
    }

    override fun store(
        sessionToken: String,
        credentials: ZettleCredentialsDTO
    ): Result<Unit> {
        return runCatching {
            storage[sessionToken] = credentials
        }
    }

    override fun remove(sessionToken: String): Result<Unit> {
        return runCatching {
            storage -= sessionToken
        }
    }
}
