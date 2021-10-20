package server.client.http

import kotlin.reflect.KClass

interface Decoding {

    fun <T : Any> decode(string: String, clazz: KClass<T>): Result<T>
}

inline fun <reified T : Any> Decoding.decode(string: String): Result<T> {
    return this.decode(string, T::class)
}
