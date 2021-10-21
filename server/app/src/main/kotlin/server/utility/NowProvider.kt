package server.utility

import java.time.Clock
import java.time.Instant

class NowProvider : NowProviding {

    override fun now(): Instant {
        return Instant.now(Clock.systemUTC())
    }
}
