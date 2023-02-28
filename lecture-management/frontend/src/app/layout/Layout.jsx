import { Container, Menu } from "semantic-ui-react";
import { Outlet, Link } from "react-router-dom";
export default function Layout() {
  return (
    <>
      <Menu>
        <Container>
          <Menu.Item header>Lecture Management</Menu.Item>
          <Menu.Item>
            <Link to="/">Users</Link>
          </Menu.Item>
          <Menu.Item>
            <Link to="/lectures">Lecture</Link>
          </Menu.Item>
        </Container>
      </Menu>
      <Container>
        <Outlet />
      </Container>
    </>
  );
}
