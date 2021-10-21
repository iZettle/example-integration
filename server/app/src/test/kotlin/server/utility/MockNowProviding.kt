package server.utility

import java.time.Instant

class MockNowProviding : NowProviding {

    lateinit var stubNow: Instant

    override fun now(): Instant {
        return stubNow
    }
}
