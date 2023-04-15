import { Container, Navbar, Nav } from "react-bootstrap";
import { Outlet } from "react-router-dom";

export default function Layout() {
    return <>
        <Navbar bg='light' expand='lg'>
            <Container>
                <Navbar.Brand href='/'> Lecture Management</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className='me-auto'>
                        <Nav.Link href='/' >User</Nav.Link>
                        <Nav.Link href='/lectures' >Lecture</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
        <Outlet />
    </>;
}