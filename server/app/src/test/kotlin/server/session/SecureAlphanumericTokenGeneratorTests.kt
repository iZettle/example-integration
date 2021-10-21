package server.session

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SecureAlphanumericTokenGeneratorTests {

    private lateinit var sut: SecureAlphanumericTokenGenerator
    private val tokenLength = 10

    @BeforeTest
    fun setUp() {
        sut = SecureAlphanumericTokenGenerator(tokenLength)
    }

    @Test
    fun `sanity check generated token`() {
        val alphanumericRegex = Regex("[A-Za-z0-9)]{10}")

        val token = sut.generate()

        assertTrue(alphanumericRegex.matches(token))
    }
}
