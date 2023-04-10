Set-Location -Path web
yarn lint
yarn test

Set-Location -Path ..\server
.\gradlew check

Set-Location -Path ..
