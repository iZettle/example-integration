package server.client.http

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.KClass

class JsonDecoder : Decoding {

    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> decode(string: String, clazz: KClass<T>): Result<T> {
        val strategy = clazz.serializerOrNull()
            ?: return Result.failure(RuntimeException("could not find serializer for type: $clazz"))

        return runCatching {
            Json.decodeFromString(strategy, string)
        }
    }
}
