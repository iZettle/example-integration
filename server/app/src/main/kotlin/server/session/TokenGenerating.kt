package server.session

interface TokenGenerating {

    fun generate(): String
}
