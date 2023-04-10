import { render, screen, waitFor } from "@testing-library/react";
import { ResponseComposition, rest, RestContext, RestRequest } from "msw";
import { setupServer } from "msw/node";
import axios, { AxiosError } from "axios";
import { Router } from "react-router-dom";
import { createMemoryHistory } from "history";
import { setLogger } from "react-query";

import Landing from "./Landing";
import TestWrapper from "../../testing/test-wrapper";
import { User } from "../../data/user";

const testUser: User = { organizationUuid: "foo", userUuid: "bar" };
const serverMeEndpoint = "/v1/me";

const server = setupServer(
  rest.get(
    serverMeEndpoint,
    (req: RestRequest, res: ResponseComposition, ctx: RestContext) =>
      res(ctx.json(testUser))
  )
);

beforeAll(() => {
  server.listen();
  setLogger({
    log: () => {},
    warn: () => {},
    error: () => {},
  });
});
afterEach(() => server.resetHandlers());
afterAll(() => {
  server.close();
});

jest.mock("../../hooks/use-oauth", () => {
  return () => ({
    generateCodeGrantUrl: () => "",
  });
});

jest.mock("../../hooks/use-environment", () => {
  return () => ({
    getEnvVar: () => "",
  });
});

test("renders header", async () => {
  render(<Landing />, { wrapper: TestWrapper });
  await waitFor(() => {
    const headerElement = screen.getByText(/Example integration/i);
    expect(headerElement).toBeInTheDocument();
  });
});

test("button click requests user data", async () => {
  const history = createMemoryHistory();
  history.replace = jest.fn();

  const getSpy = jest.spyOn(axios, "get");

  render(
    <Router location={history.location} navigator={history}>
      <Landing />
    </Router>,
    { wrapper: TestWrapper }
  );

  const fetchButtonElement = screen.getByText(/Log in with Zettle/i);
  fetchButtonElement.click();

  await waitFor(() => {
    expect(getSpy).toHaveBeenCalledWith(serverMeEndpoint, {
      withCredentials: true,
    });
    expect(history.replace).toHaveBeenCalledWith("/welcome");
  });
});

test("should start auth flow on 401", async () => {
  const error: Partial<AxiosError> = {
    response: {
      status: 401,
      data: null,
      statusText: "",
      headers: {},
      config: {},
    },
  };

  Object.defineProperty(window, "location", {
    writable: true,
    value: { assign: jest.fn() },
  });

  const getSpy = jest.spyOn(axios, "get").mockRejectedValueOnce(error);
  const locationAssignSpy = jest.spyOn(window.location, "assign");

  render(<Landing />, { wrapper: TestWrapper });

  const fetchButtonElement = screen.getByText(/Log in with Zettle/i);
  fetchButtonElement.click();

  await waitFor(() => {
    expect(getSpy).toHaveBeenCalledWith("/v1/me", {
      withCredentials: true,
    });
    expect(locationAssignSpy).toHaveBeenCalled();
  });
});
