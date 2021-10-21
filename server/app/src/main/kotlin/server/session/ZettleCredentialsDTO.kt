package server.session

import java.time.Instant

data class ZettleCredentialsDTO(
    val accessToken: String,
    val accessTokenExpiresAt: Instant,
    val refreshToken: String
)
