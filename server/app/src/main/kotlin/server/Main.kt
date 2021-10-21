package server

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.sessions.SessionStorageMemory
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.slf4j.event.Level
import server.client.http.JsonDecoder
import server.client.http.OkHttpRequestSender
import server.client.zettle.oauth.ZettleCodeGrantExchanger
import server.client.zettle.user.ZettleUserFetcher
import server.oauth.InMemoryOAuthStateStore
import server.route.get
import server.route.handler.GetMeRouteHandler
import server.route.handler.HelloWorldRouteHandler
import server.route.handler.OAuthGrantRedirectRouteHandler
import server.route.handler.OAuthStartLoginRouteHandler
import server.session.InMemoryZettleSessionCredentialStore
import server.session.KtorSessionCookie
import server.session.SecureAlphanumericTokenGenerator
import server.utility.NowProvider
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore
import java.time.Duration

fun main() {
    val server = configureServer()
    server.application.configureRouting()
    server.start(wait = true)
}

fun Application.build() {
    install(ContentNegotiation) {
        json()
    }
    install(CallLogging) {
        level = Level.INFO
    }
    install(Sessions) {
        cookie<KtorSessionCookie>("session", SessionStorageMemory())
    }
    install(CORS) {
        header(HttpHeaders.Authorization)
        allowCredentials = true
        host("localhost:3000", listOf("https"))
    }
}

private fun configureServer(): NettyApplicationEngine {
    val instance = KeyStore.getInstance("JKS")
    val keyStoreFile = File(".cert/keystore.jks")
    val certPass = "example-password"
    instance.load(FileInputStream(keyStoreFile), certPass.toCharArray())

    val environment = applicationEngineEnvironment {
        connector {
            port = 8000
        }
        sslConnector(
            keyStore = instance,
            keyAlias = "testCert",
            keyStorePassword = { certPass.toCharArray() },
            privateKeyPassword = { certPass.toCharArray() }
        ) {
            port = 8001
            keyStorePath = keyStoreFile
        }
        module(Application::build)
    }

    return embeddedServer(
        Netty,
        environment
    )
}

private fun Application.configureRouting() {
    val oauthBaseUrl = readEnvVarOrThrow("ZETTLE_OAUTH_BASE_URL").toHttpUrl()
    val oauthClientId = readEnvVarOrThrow("ZETTLE_OAUTH_CLIENT_ID")
    val clientSecret = readEnvVarOrThrow("ZETTLE_OAUTH_CLIENT_SECRET")
    val oauthScope = "READ:USERINFO"

    val requestSender = OkHttpRequestSender()
    val jsonDecoder = JsonDecoder()
    val userFetcher = ZettleUserFetcher(oauthBaseUrl, requestSender, jsonDecoder)
    val inMemorySessionStore = InMemoryZettleSessionCredentialStore()
    val sessionTokenGenerator = SecureAlphanumericTokenGenerator(tokenLength = 32)
    val stateTokenGenerator = SecureAlphanumericTokenGenerator(tokenLength = 16)
    val nowProvider = NowProvider()
    val oauthStateStore = InMemoryOAuthStateStore(nowProvider, stateDuration = Duration.ofSeconds(30))

    val oauthRedirectUri = "https://localhost:8001/auth/redirect"
    val homeRedirectUri = "https://localhost:3000/welcome"
    val grantExchanger = ZettleCodeGrantExchanger(
        oauthBaseUrl,
        requestSender,
        jsonDecoder,
        oauthRedirectUri,
        clientId = oauthClientId,
        clientSecret = clientSecret
    )

    val helloWorldRouteHandler = HelloWorldRouteHandler()
    val getMeRouteHandler = GetMeRouteHandler(userFetcher, inMemorySessionStore)
    val oauthGrantRouteHandler = OAuthGrantRedirectRouteHandler(grantExchanger, inMemorySessionStore, sessionTokenGenerator, homeRedirectUri, oauthStateStore)
    val oauthStartLoginHandler = OAuthStartLoginRouteHandler(stateTokenGenerator, oauthStateStore, oauthBaseUrl, oauthClientId, oauthRedirectUri, oauthScope)

    routing {
        get("/", helloWorldRouteHandler)
        get("/v1/me", getMeRouteHandler)
        get("/auth/redirect", oauthGrantRouteHandler)
        get("/auth/login", oauthStartLoginHandler)
    }
}

private fun readEnvVarOrThrow(key: String): String {
    return System.getenv()[key]
        ?: throw RuntimeException("you must set the $key env var")
}
