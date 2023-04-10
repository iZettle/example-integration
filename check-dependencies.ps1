# Check if the needed tools are installed
$oldPreference = $ErrorActionPreference
$ErrorActionPreference = ‘stop’

$DockerExists=$false
try {if(Get-Command docker) {
    $DockerExists=$true
}} Catch {“docker not installed”}

$NpmExists=$false
try {if(Get-Command npm) {
    $NpmExists=$true
}} Catch {“npm not installed”}

$YarnExists=$false
try {if(Get-Command yarn) {
    $YarnExists=$true
}} Catch {“yarn not installed”}

$ChocoExists=$false
try {if(Get-Command choco) {
    $ChocoExists=$true
}} Catch {“choco not installed”}

$MkcertExists=$false
try {if(Get-Command mkcert) {
    $MkcertExists=$true
}} Catch {“mkcert not installed”}

$OpensslExists=$false
try {if(Get-Command openssl) {
    $OpensslExists=$true
}} Catch {“openssl not installed”}
$ErrorActionPreference=$oldPreference

if($DockerExists -and $NpmExists -and $YarnExists -and $ChocoExists -and $MkcertExists -and $OpensslExists) {
    Write-Output "Dependencies docker, npm, yarn, choco, mkcert and openssl are present"
    exit 0
}

if(!$DockerExists -or !$NpmExists -or !$ChocoExists) {
    if(!$DockerExists) {
        Write-Output "Please download and install Docker from https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe";
    }

    if(!$NpmExists -or !$ChocoExists) {
        Write-Output "Please download and install NodeJS package including chocolade from https://nodejs.org/en/download";
    }
    exit 1;
}

if (!([Security.Principal.WindowsPrincipal] `
  [Security.Principal.WindowsIdentity]::GetCurrent() `
  ).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    Write-Output "Installing dependencies requires elevated shell";
    exit 1;
}

if(!$YarnExists) {
    npm install --global yarn
}

if(!$MkcertExists) {
    choco install -y mkcert
}

if(!$OpensslExists) {
    choco install -y openssl
}

RefreshEnv
