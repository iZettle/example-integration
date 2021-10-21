package server.oauth

import java.time.Instant

data class OAuthStateDTO(
    val state: String,
    val createdAt: Instant
)
