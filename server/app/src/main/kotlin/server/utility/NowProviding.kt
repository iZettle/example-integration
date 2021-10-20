package server.utility

import java.time.Instant

interface NowProviding {

    fun now(): Instant
}
