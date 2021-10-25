import { Button, ButtonGroup, Heading, VStack } from "@chakra-ui/react";
import { useHistory } from "react-router-dom";
import { AxiosError } from "axios";

import "./Landing.css";
import { useUser } from "../../hooks/use-user";
import { QueryObserverResult } from "react-query";
import { User } from "../../data/user";
import useOAuth from "../../hooks/use-oauth";

function Landing() {
  const user = useUser();
  const history = useHistory();
  const { generateCodeGrantUrl } = useOAuth();

  const onButtonClick = async () => {
    user
      .refetch()
      .then((result: QueryObserverResult<User, AxiosError>) => {
        if (result.error) {
          return Promise.reject(result.error);
        }
        history.replace("/welcome", { user: result.data });
      })
      .catch((e: AxiosError) => {
        if (e.response?.status === 401) {
          window.location.assign(generateCodeGrantUrl());
        }
      });
  };

  return (
    <VStack spacing="24px" className="App">
      <Heading>Example integration</Heading>
      <ButtonGroup spacing="6">
        <Button
          colorScheme="blue"
          backgroundColor="midnightblue"
          textColor="white"
          isLoading={user.isLoading}
          onClick={() => onButtonClick()}
        >
          Log in with Zettle
        </Button>
      </ButtonGroup>
    </VStack>
  );
}

export default Landing;
