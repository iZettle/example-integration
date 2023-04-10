$KEY=".cert/localhost.key"
$CERT=".cert/localhost.pem"
$KEYSTORE="server/.cert/keystore.jks"

if ((Test-Path "$KEY") -and (Test-Path "$CERT") -and (Test-Path "$KEYSTORE") -and (Test-Path "web/$CERT")) {
  Write-Output "Key and cert exist and will not be recreated";
  exit 0;
}

if (!([Security.Principal.WindowsPrincipal] `
  [Security.Principal.WindowsIdentity]::GetCurrent() `
  ).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    Write-Output "Generating certificate requires elevated shell";
    exit 1;
}

Write-Output "Generating new TLS certificate for localhost"

mkcert -install
if (!(Test-Path ".cert")) {
  mkdir -p .cert
}
mkcert -key-file $KEY -cert-file $CERT "localhost"

if (!(Test-Path "web/.cert")) {
  mkdir -p web/.cert
}
Copy-Item $KEY web/.cert/localhost.key
Copy-Item $CERT web/.cert/localhost.pem

Write-Output "Converting generated certificate for use in server project"

if (!(Test-Path "server/.cert")) {
  mkdir -p server/.cert
}

$openssl = 'C:\Program Files\Git\usr\bin\openssl'
& $openssl pkcs12 -export -in "$CERT" -inkey "$KEY" -out server/.cert/keystore.p12 -name "testCert" -password "pass:example-password"
keytool -importkeystore -noprompt -srckeystore server/.cert/keystore.p12 -srcstoretype pkcs12 -srcstorepass "example-password" -destkeystore server/.cert/keystore.jks -deststorepass "example-password" -destkeypass "example-password"
