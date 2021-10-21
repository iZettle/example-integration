package server.oauth

import server.NotStubbedException

class MockOAuthStateStoring : OAuthStateStoring {

    var stubFetch: Result<OAuthStateDTO?> = Result.failure(NotStubbedException)
    var spyFetchState: String? = null
    var stubStore: Result<Unit> = Result.failure(NotStubbedException)
    var spyStoreState: String? = null

    override fun fetch(state: String): Result<OAuthStateDTO?> {
        spyFetchState = state
        return stubFetch
    }

    override fun store(state: String): Result<Unit> {
        spyStoreState = state
        return stubStore
    }
}
