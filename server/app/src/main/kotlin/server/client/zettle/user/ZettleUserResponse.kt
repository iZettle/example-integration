package server.client.zettle.user

import kotlinx.serialization.Serializable
import server.client.http.UUIDSerializer
import java.util.UUID

@Serializable
data class ZettleUserResponse(
    @Serializable(with = UUIDSerializer::class) val uuid: UUID,
    @Serializable(with = UUIDSerializer::class) val organizationUuid: UUID
)
