import { render, screen, waitFor } from "@testing-library/react";
import { createMemoryHistory } from "history";
import { Router } from "react-router-dom";

import Welcome from "./Welcome";
import TestWrapper from "../../testing/test-wrapper";

test("redirects when user is missing", () => {
  const history = createMemoryHistory();
  render(
    <Router history={history}>
      <Welcome />
    </Router>,
    { wrapper: TestWrapper }
  );
  expect(history.location.pathname).toBe("/");
});

test("renders user passed by state", async () => {
  const history = createMemoryHistory();
  history.location.state = {
    user: { organizationUuid: "foo", displayName: "bar" },
  };

  render(
    <Router history={history}>
      <Welcome />
    </Router>,
    { wrapper: TestWrapper }
  );

  await waitFor(() => {
    const orgIdElement = screen.getByText(/Org id: foo/i);
    const displayNameElement = screen.getByText(/Display name: bar/i);
    expect(orgIdElement).toBeInTheDocument();
    expect(displayNameElement).toBeInTheDocument();
  });
});
