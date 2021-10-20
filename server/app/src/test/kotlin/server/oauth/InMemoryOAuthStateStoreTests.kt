package server.oauth

import server.utility.MockNowProviding
import java.time.Duration
import java.time.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryOAuthStateStoreTests {

    private lateinit var sut: InMemoryOAuthStateStore
    private lateinit var mockNowProvider: MockNowProviding
    private val stateDuration = Duration.ofMinutes(1)

    @BeforeTest
    fun setUp() {
        mockNowProvider = MockNowProviding()
        sut = InMemoryOAuthStateStore(
            mockNowProvider,
            stateDuration
        )
    }

    @Test
    fun `fetching from an empty store returns null`() {
        val result = sut.fetch("nonexistent state")

        assertEquals(Result.success(null), result)
    }

    @Test
    fun `fetching an expired state returns null`() {
        val now = Instant.now()
        mockNowProvider.stubNow = now.minus(Duration.ofMinutes(2))
        sut.store("state")

        mockNowProvider.stubNow = now
        val result = sut.fetch("state")

        assertEquals(Result.success(null), result)
    }

    @Test
    fun `fetching a valid state returns it`() {
        val now = Instant.now()
        mockNowProvider.stubNow = now
        sut.store("state")

        val result = sut.fetch("state")

        val expectedResult = OAuthStateDTO("state", now)
        assertEquals(Result.success(expectedResult), result)
    }
}
