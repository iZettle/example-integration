# Example Integration - Server

## Dependencies

You'll need a copy of JDK 16 to compile the server:

* Install [SDKMAN](https://sdkman.io/) and open a new terminal
* Install OpenJDK 16: `sdk install java 16.0.2-open`
* Verify that it's selected: `sdk current java` should say `Using java version 16.0.2-open`

You must also have registered a public Zettle application, and noted its client ID and client secret: https://developer.zettle.com/
The client ID and client secret are provided to the backend service as environment variables: `ZETTLE_OAUTH_CLIENT_ID` and `ZETTLE_OAUTH_CLIENT_SECRET`

## Running the server

* Open `server` folder with IntelliJ IDEA (Community or Ultimate)
* Run `Server` target to start the server on port 8001
  * You must also replace the `ZETTLE_OAUTH_CLIENT_ID` and `ZETTLE_OAUTH_CLIENT_SECRET` environment variables as noted above 

Sessions are stored in memory, so you'll be logged out if you restart the backend service.

## Docker

* Build an image: `./build-image.sh` will produce an image with the tag `example-integration-server:test`
* Run the image: `docker run -it -e ZETTLE_OAUTH_BASE_URL="https://oauth.zettle.com" -e ZETTLE_OAUTH_CLIENT_ID="replace with oauth client id" -e ZETTLE_OAUTH_CLIENT_SECRET="replace with oauth client secret" -p 8001:8001 example-integration:test`

## Terminal

* Running server: `./gradlew run`
* Running tests: `./gradlew test`
* Linting: `./gradlew ktlintCheck`
* Formatting: `./gradlew ktlintFormat`

## Example output

Using [httpie](https://httpie.io/):

```
→ http localhost:8001
HTTP/1.1 200 OK
Connection: keep-alive
Content-Length: 13
Content-Type: text/plain; charset=UTF-8

Hello, world!
```

Running the Docker image:

```
→ docker run -it -e ZETTLE_OAUTH_BASE_URL="https://oauth.zettle.com" -e ZETTLE_OAUTH_CLIENT_ID="replace with oauth client id" -e ZETTLE_OAUTH_CLIENT_SECRET="replace with oauth client secret" -p 8001:8001 example-integration:test
[main] INFO ktor.application - Autoreload is disabled because the development mode is off.
[main] INFO ktor.application - Responding at http://0.0.0.0:8001
[main] INFO ktor.application - Application started in 0.066 seconds.
```