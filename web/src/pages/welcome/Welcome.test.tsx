import { render, screen, waitFor } from "@testing-library/react";
import { setLogger } from "react-query";
import { Router } from "react-router-dom";
import { setupServer } from "msw/node";
import { ResponseComposition, rest, RestContext, RestRequest } from "msw";
import { createMemoryHistory } from "history";

import Welcome from "./Welcome";
import TestWrapper from "../../testing/test-wrapper";
import { User } from "../../data/user";

const testUser: User = { organizationUuid: "foo", userUuid: "bar" };
const serverMeEndpoint = "https://localhost:8001/v1/me";

const server = setupServer();

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

test("redirects when user request errors", async () => {
  const history = createMemoryHistory();
  history.replace = jest.fn();

  Object.defineProperty(window, "location", {
    writable: true,
    value: { assign: jest.fn() },
  });

  server.use(
    rest.get(
      serverMeEndpoint,
      (req: RestRequest, res: ResponseComposition, ctx: RestContext) => {
        return res(ctx.status(500), ctx.json("error"));
      }
    )
  );

  render(
    <Router location={history.location} navigator={history}>
      <Welcome />
    </Router>,
    { wrapper: TestWrapper }
  );

  await waitFor(() => {
    expect(history.replace).toHaveBeenCalledWith("/");
  });
});

test("renders user from request", async () => {
  const history = createMemoryHistory();

  server.use(
    rest.get(
      serverMeEndpoint,
      (req: RestRequest, res: ResponseComposition, ctx: RestContext) =>
        res(ctx.json(testUser))
    )
  );

  render(
    <Router location={history.location} navigator={history}>
      <Welcome />
    </Router>,
    { wrapper: TestWrapper }
  );

  await waitFor(() => {
    const orgIdElement = screen.getByText(/Org id: foo/i);
    const uuidElement = screen.getByText(/User id: bar/i);
    expect(orgIdElement).toBeInTheDocument();
    expect(uuidElement).toBeInTheDocument();
  });
});
