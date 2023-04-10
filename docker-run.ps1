if (!(Test-Path "server\.env") -and !(Test-Path "web\.env")) {
	Write-Warning "Please populate .env files as described in README.md"
	exit 1
}

.\check-dependencies.ps1
if ($LASTEXITCODE -ne 0) {
	exit $LASTEXITCODE
}

.\create-certificate.ps1
if ($LASTEXITCODE -ne 0) {
	exit $LASTEXITCODE
}

Set-Location -Path web 
.\build-image.ps1
if ($LASTEXITCODE -ne 0) {
	exit $LASTEXITCODE
}

Set-Location -Path ..\server 
.\build-image.ps1
if ($LASTEXITCODE -ne 0) {
	exit $LASTEXITCODE
}

Set-Location -Path ..
docker compose up -d
