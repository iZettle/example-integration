package server.session

class MockTokenGenerating : TokenGenerating {

    var stubGenerate: String? = null

    override fun generate(): String {
        return stubGenerate!!
    }
}
