package server.session

import java.util.concurrent.ConcurrentHashMap

class InMemoryZettleSessionCredentialStore : ZettleSessionCredentialStoring, ZettleAccessTokenStoring {

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

    override fun fetchByAccessToken(accessToken: String): Result<ZettleCredentialsDTO?> {
        return runCatching {
            storage.values.firstOrNull {
                it.accessToken == accessToken
            }
        }
    }

    override fun storeByAccessToken(
        accessToken: String,
        credentials: ZettleCredentialsDTO
    ): Result<Unit> {
        return runCatching {
            val entry = storage.entries.firstOrNull { entry ->
                entry.value.accessToken == accessToken
            } ?: return Result.failure(RuntimeException("failed to find existing access token"))

            storage[entry.key] = credentials
        }
    }

    override fun removeByAccessToken(accessToken: String): Result<Unit> {
        return runCatching {
            val entry = storage.entries.firstOrNull { entry ->
                entry.value.accessToken == accessToken
            } ?: return Result.failure(RuntimeException("failed to find existing access token"))

            storage -= entry.key
        }
    }
}
