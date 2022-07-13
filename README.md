# Zettle Example Integration

- [Supported regions](#supported-regions)
- [Introduction](#introduction)
  - [Structure](#structure)
- [Prerequisites](#prerequisites)
- [Instructions](#instructions)
  - [Step 1: Environment variables](#step-1-environment-variables)
  - [Step 2: Running via Docker](#step-2-running-via-docker)
- [Subprojects](#subprojects)
- [Certificates](#certificates)

## Supported regions

Zettle provides APIs for you to integrate Zettle Go with your services.

> **Note:** The API documentation is currently in the beta phase.

Currently, Zettle provides APIs for the following markets:

- United Kingdom
- Sweden
- Brazil
- Norway
- Denmark
- Finland
- Germany
- Mexico
- Netherlands
- France
- Spain
- Italy

## Introduction

This is an example project to help integrators get started, and understand the basics of Zettle’s public API.

You'll be able to clone this repository down and follow instructions to start web and backend components, so that you can perform a basic function on your account. For example, you can "Log in with Zettle", which will create a session allowing you to make requests to Zettle's other APIs such as Purchases or Merchant reports.

This code is intended to be used as reference material rather than being copied verbatim.

### Structure

This repository contains everything integrators need to run the project in one place, and is split into three sections:

* Top level: scripts and Docker-related files to make running everything simple
* `web`: everything relating to the React frontend, to start OAuth flows and display content to the user.
* `server`: everything related to the backend service, which makes calls to Zettle APIs and manages user sessions.

The first version is designed to be run on macOS (we assume the presence of `brew`), but could be extended to run on other operating systems too, if there's demand.

## Prerequisites

* You have an account for the [Developer Portal](https://developer.zettle.com/). If you don't have an account, [sign up for a developer account](https://developer.zettle.com/register).
* You have API credentials for the app. If you don't have any, [get API credentials for authorisation code grant](https://developer.zettle.com/applications/create/public).
* Note: Use following values when you register your API credentials
  * Oauth Redirect URIs: `https://localhost:8001/auth/redirect`
  * App URL: `https://localhost:3000/welcome`

## Instructions

### Step 1: Environment variables

Prepare environment variables in each subproject, so that the example app can use your developer credentials.

#### Server

* Copy `/server/.env.example` to `/server/.env`
* Modify the variables that say `CHANGE_ME` to your own details

Detailed description of the variables and what they do:


| Environment Variable       | Value                      | Notes                                 |
| ---------------------------- | ---------------------------- | --------------------------------------- |
| ZETTLE_OAUTH_BASE_URL      | `https://oauth.zettle.com` |                                       |
| ZETTLE_OAUTH_CLIENT_ID     | UUID string                | The client ID of your public app      |
| ZETTLE_OAUTH_CLIENT_SECRET | String                     | The client secret for your public app |

#### Web

* Copy `/web/.env.example` to `/web/.env`
* You don't need to change anything in this file

Detailed description of the variables and what they do:


| Environment Variable  | Value            | Notes                                                    |
| ----------------------- | ------------------ | ---------------------------------------------------------- |
| HTTPS                 | Boolean          | Required to be true to use Zettle auth                   |
| SSL_CRT_FILE          | File path string | For development server use                               |
| SSL_KEY_FILE          | File path string | For development server use                               |
| DISABLE_ESLINT_PLUGIN | Boolean          | Set to false to enforce lint/format rules                |
| REACT_APP_SERVER_URL  | URL:PORT string  | Change this if you run the server at a different address |

### Step 2: Running via Docker

To start both the server and web frontend you can execute the `./docker-run.sh` script in the root of this repository.

This script:

* Creates necessary self-signed TLS certificates
* Builds the web app
* Builds the server app
* Runs both in Docker containers

You should see that Docker has run both containers when the script finishes, like so:

```
+ docker compose up -d
[+] Running 2/2
 ⠿ Container example-integration-server-1  Started                                                               0.5s
 ⠿ Container example-integration-web-1     Started                                                               0.5s
```

Now the example app will be running and you can view it in your browser at https://localhost:3000/

## Subprojects

If you encounter problems running the project with Docker, you can set things up manually by following the README files in the web and server subprojects:

- [Web app readme](./web/README.md#installing-dependencies)
- [Server readme](./server/README.md#dependencies)

## Certificates

The project uses a tool called `mkcert` to install self-signed certificates to use in both the server and web projects. It's automatically installed during the `docker-run.sh` process, and marks these certificates as valid in your browser (tested by us with Firefox).

If you want to run either of the projects independently, you'll need to run `create-certificate.sh` once to generate the required certificates first.
