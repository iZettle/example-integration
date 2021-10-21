import useEnvironment from "./use-environment";

const useOAuth = () => {
  const { getEnvVar } = useEnvironment();

  const serverUrl = getEnvVar("REACT_APP_SERVER_URL");

  const generateCodeGrantUrl = () => {
    return `${serverUrl}/auth/login`;
  };

  return {
    generateCodeGrantUrl,
  };
};

export default useOAuth;
