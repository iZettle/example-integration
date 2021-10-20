# Zettle Example Integration

- [Introduction](#introduction) 
- [Structure](#structure)
- [Instructions](#instructions)
  - [Dependencies](#dependencies)
  - [Environment variables](#environment-variables)
  - [Docker](#running-via-docker)
- [Certificates](#certificates)

## Introduction

This is an example project to help integrators get started, and understand the basics of Zettle’s public API.

You'll be able to clone this repository down and follow instructions to start web and backend components, so that you can perform a basic function on your account. For example, you can "Log in with Zettle", which will create a session allowing you to make requests to Zettle's other APIs such as Purchases or Merchant reports.

This code is intended to be used as reference material rather than being copied verbatim.

## Structure

This repository contains everything integrators need to run the project in one place, and is split into three sections:

* Top level: scripts and Docker-related files to make running everything simple
* `web`: everything relating to the React frontend, to start OAuth flows and display content to the user.
* `server`: everything related to the backend service, which makes calls to Zettle APIs and manages user sessions.

The first version is designed to be run on macOS (we assume the presence of `brew`), but could be extended to run on other operating systems too, if there's demand.

## Instructions
### Dependencies
Prior to starting the project locally you'll need to install dependencies for the server and the web app. Instructions on doing this are:

- [Web app readme](./web/README.md#installing-dependencies)
- [Server readme](./server/README.md#dependencies)

If you encounter difficulties running the project via Docker, ensure the above steps are completed first. 

### Environment variables
There are some settings that need to be populated in each project so that the code can use your [developer.zettle.com](https://developer.zettle.com) credentials.

You should create a Public API Application here to get these values https://developer.zettle.com/applications/create/public 

#### Web
These are set in `/web/.env`. An example set of values is provided at `/web/.env.example`

| Environment Variable| Value | Notes |
|----|-----|----|
| HTTPS | Boolean | Required to be true to use Zettle auth |
| SSL_CRT_FILE | File path string | For development server use |
| SSL_KEY_FILE | File path string | For development server use |
| DISABLE_ESLINT_PLUGIN | Boolean | Set to false to enforce lint/format rules |
| REACT_APP_SERVER_URL | URL:PORT string | Change this if you run the server at a different address |
| REACT_APP_ZETTLE_OAUTH_URL | https://oauth.zettle.com | |
| REACT_APP_ZETTLE_OAUTH_CLIENT_ID | UUID string | The client ID within your public app |

#### Server
These are set in `/server/.env`. An example set of values is provided at `/server/.env.example`

| Environment Variable| Value | Notes |
|----|-----|----|
| ZETTLE_OAUTH_BASE_URL | https://oauth.zettle.com | |
| ZETTLE_OAUTH_CLIENT_ID | UUID string | The client ID within your public app |
| ZETTLE_OAUTH_CLIENT_SECRET | String |The client secret within your public app |

### Running via Docker
To start both the server and web frontend you can execute the `./docker-run.sh` script in the root of this repository.
This will create docker images for both components of the example integration and then run them. You can verify that
the apps are running in the docker dashboard following the completion of the script.

## Certificates
The project uses a tool called `mkcert` to install self-signed certificates to use in both the server and web projects. It's automatically installed during the `docker-run.sh` process, and marks these certificates as valid in your browser (tested by us with Firefox).

If you want to run either of the projects independently, you'll need run `create-certificate.sh` once to generate the required certificates first.
