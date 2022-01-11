# Example integration single page app

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

- [Installing dependencies](#installing-dependencies)
- [Running the web app](#running-the-web-app)
    - [Local server](#dev-server-via-yarn-start)
    - [Docker](#docker)
- [Other scripts](#other-scripts)
    
## Installing dependencies
We typically use [Yarn](https://yarnpkg.com/) for fetching dependencies and running scripts. If you prefer NPM that
would work nicely too. To pull down dependencies simply run `yarn install` or `npm install` from inside the web folder of this repo. 

## Running the web app

### Docker
If you would like to run the project with Docker, follow the instructions on the [main README](../README.md#running-via-docker).


### Dev server via `yarn start`

Runs the app in the development mode. Prior to doing this, you will need to do a few bits of housekeeping:

- Re-name `.env.example` to `.env`. This file contains some basic settings for easy local development.
- Ensure that you add `https://localhost:8001/auth/redirect` to your OAuth Redirect URIs within [developer.zettle.com](https://developer.zettle.com).

- Prior to the first use of the app you should also run the `create-certificate.sh` script in the main project directory.
For more detail [see here](../README.md#certificates).

Running the start script should open [https://localhost:3000](https://localhost:3000) in your default browser. The page will reload if you make changes to the code.

## Other Scripts

In the project directory, you can run:

### `yarn test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `yarn build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

### `yarn lint` && `yarn lint:fix`

Runs eslint with some basic React-supporting rules. The second command can be \
used to automatically resolve minor issues like semi-colons and inconsistent \
use of quote marks.
