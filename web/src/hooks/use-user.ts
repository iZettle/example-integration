import { useQuery } from "react-query";
import axios, { AxiosError, AxiosResponse } from "axios";

import { User } from "../data/user";
import useEnvironment from "./use-environment";

export const useUser = () => {
  const { getEnvVar } = useEnvironment();

  const serverUrl = getEnvVar("REACT_APP_SERVER_URL");

  const fetchUser = async () =>
    axios
      .get(`${serverUrl}/v1/me`, { withCredentials: true })
      .then((res: AxiosResponse<User>) => res.data)
      .catch((e: AxiosError) => Promise.reject(e));

  const query = useQuery<User, AxiosError>("fetchUser", fetchUser, {
    enabled: false,
    refetchOnWindowFocus: false,
    retry: false,
  });

  return { ...query };
};
