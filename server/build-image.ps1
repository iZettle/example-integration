./gradlew clean shadowJar

docker build -t example-integration-server:test `
 --build-arg jar=app/build/libs/server-all.jar `
 --build-arg keystore=.cert/keystore.jks `
 .
