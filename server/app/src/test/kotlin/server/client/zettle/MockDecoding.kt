package server.client.zettle

import server.client.http.Decoding
import kotlin.reflect.KClass

class MockDecoding : Decoding {

    var stubDecode: Any? = null

    override fun <T : Any> decode(string: String, clazz: KClass<T>): Result<T> {
        if (!clazz.isInstance(stubDecode)) {
            return Result.failure(RuntimeException("wrong type passed to stub: $stubDecode"))
        }
        @Suppress("UNCHECKED_CAST")
        return Result.success(stubDecode as T)
    }
}
