import { Box, Heading, Spinner, Text, VStack } from "@chakra-ui/react";
import { AxiosError } from "axios";
import { useHistory } from "react-router-dom";

import "./Welcome.css";
import { useUser } from "../../hooks/use-user";
import { QueryObserverResult } from "react-query";
import { User } from "../../data/user";

function Welcome() {
  const history = useHistory();
  const user = useUser();

  if (!user.data) {
    user.refetch().then((result: QueryObserverResult<User, AxiosError>) => {
      if (result.error) {
        history.replace("/");
      }
    });
  }

  if (user.isLoading || !user.data) {
    return (
      <VStack spacing="24px">
        <Spinner data-testid="spinner" marginTop={"50px"} />
      </VStack>
    );
  }

  return (
    <VStack spacing="24px">
      <Heading>Welcome</Heading>
      <Box alignContent="left" width="50%">
        <Text>Org id: {user.data.organizationUuid}</Text>
        <Text>Uuid: {user.data.uuid}</Text>
      </Box>
    </VStack>
  );
}

export default Welcome;
