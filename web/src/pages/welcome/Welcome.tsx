import { Box, Heading, Text, VStack } from "@chakra-ui/react";
import { useHistory } from "react-router-dom";

import "./Welcome.css";
import { User } from "../../data/user";

function Welcome() {
  const history = useHistory();
  const state = history.location.state as WelcomeState;

  if (!state?.user) {
    history.replace("/");
    return <></>;
  }

  return (
    <VStack spacing="24px">
      <Heading>Welcome</Heading>
      <Box alignContent="left" width="50%">
        <Text>Org id: {state.user.organizationUuid}</Text>
        <Text>Display name: {state.user.displayName}</Text>
      </Box>
    </VStack>
  );
}

interface WelcomeState {
  user: User;
}

export default Welcome;
