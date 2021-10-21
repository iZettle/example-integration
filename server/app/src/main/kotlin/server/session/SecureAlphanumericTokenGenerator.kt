package server.session

import java.security.SecureRandom

class SecureAlphanumericTokenGenerator(
    private val tokenLength: Int
) : TokenGenerating {

    companion object {
        private val characters = listOf(('A'..'Z') + ('a'..'z') + ('0'..'9')).flatten()
    }

    private val random = SecureRandom()

    override fun generate(): String {
        val stringBuilder = StringBuilder()
        repeat(tokenLength) {
            val character = characters[random.nextInt(characters.size)]
            stringBuilder.append(character)
        }
        return stringBuilder.toString()
    }
}
