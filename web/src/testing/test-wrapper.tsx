import { QueryClient, QueryClientProvider } from "react-query";
import { ChakraProvider } from "@chakra-ui/react";
import { ReactNode } from "react";

const queryClient = new QueryClient();

const TestWrapper = ({ children }: { children?: ReactNode }): JSX.Element => (
  <ChakraProvider>
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  </ChakraProvider>
);

export default TestWrapper;
