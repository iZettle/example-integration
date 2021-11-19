package server.client.zettle.product

import kotlinx.serialization.Serializable
import server.client.http.UUIDSerializer
import java.util.UUID
import kotlinx.serialization.json.Json

@Serializable
data class ZettleProductResponse(
        @Serializable val productCount: Integer
)
