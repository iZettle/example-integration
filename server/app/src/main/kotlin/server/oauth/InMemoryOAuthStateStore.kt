package server.oauth

import server.utility.NowProviding
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class InMemoryOAuthStateStore(
    private val nowProvider: NowProviding,
    private val stateDuration: Duration
) : OAuthStateStoring {

    private val storage = ConcurrentHashMap<String, OAuthStateDTO>()

    override fun fetch(state: String): Result<OAuthStateDTO?> {
        return runCatching {
            val dto = storage[state]
                ?: return Result.success(null)

            val expiresAt = dto.createdAt.plus(stateDuration)
            if (expiresAt.isBefore(nowProvider.now())) {
                storage -= state
                return Result.success(null)
            }

            return Result.success(dto)
        }
    }

    override fun store(state: String): Result<Unit> {
        val dto = OAuthStateDTO(
            state = state,
            createdAt = nowProvider.now()
        )
        return runCatching {
            storage[state] = dto
        }
    }
}
