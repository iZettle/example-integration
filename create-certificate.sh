#!/usr/bin/env bash
set -euxo pipefail

KEY=.cert/localhost.key
CERT=.cert/localhost.pem
KEYSTORE=server/.cert/keystore.jks

if [[ -f "$KEY" && -f "$CERT" && -f "$KEYSTORE" && -f "web/$CERT" ]]; then
  echo "Key and cert exist and will not be recreated";
  exit 0;
fi

echo "Generating new TLS certificate for localhost"

brew list mkcert || (brew install mkcert && echo "mkcert will ask for permission to install a new certificate authority on your system")
brew list nss || (brew install nss && echo "Installing nss for mkcert Firefox integration")
mkcert -install
mkdir -p .cert
mkcert -key-file $KEY -cert-file $CERT "localhost"

mkdir -p web/.cert
cp $KEY web/.cert/localhost.key
cp $CERT web/.cert/localhost.pem

echo "Converting generated certificate for use in server project"

mkdir -p server/.cert

openssl pkcs12 -export -in "$CERT" -inkey "$KEY" -out server/.cert/keystore.p12 -name "testCert" -password "pass:example-password"
keytool -importkeystore -noprompt -srckeystore server/.cert/keystore.p12 -srcstoretype pkcs12 -srcstorepass "example-password" -destkeystore server/.cert/keystore.jks -deststorepass "example-password" -destkeypass "example-password"
