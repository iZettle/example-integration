package server.oauth

interface OAuthStateStoring {

    fun fetch(state: String): Result<OAuthStateDTO?>
    fun store(state: String): Result<Unit>
}
