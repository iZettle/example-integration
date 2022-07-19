import { Box, Heading, Spinner, Text, VStack } from "@chakra-ui/react";
import { AxiosError } from "axios";
import { useNavigate } from "react-router-dom";

import "./Welcome.css";
import { useUser } from "../../hooks/use-user";
import { QueryObserverResult } from "react-query";
import { User } from "../../data/user";

function Welcome() {
  const navigate = useNavigate();
  const user = useUser();

  if (!user.data) {
    user.refetch().then((result: QueryObserverResult<User, AxiosError>) => {
      if (result.error) {
        navigate("/");
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
        <Text>User id: {user.data.userUuid}</Text>
      </Box>
    </VStack>
  );
}

export default Welcome;
