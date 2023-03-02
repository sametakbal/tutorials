import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Spinner from 'react-bootstrap/Spinner';
import { useEffect, useState } from 'react';
import { Modal } from 'react-bootstrap';

export default function User() {
  const [isLoading, setIsLoading] = useState(false);
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState({
    identityNo: '',
    name: '',
    surname: '',
    gender: '',
    role: '',
  });
  const [show, setShow] = useState(false);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  useEffect(() => {
    setIsLoading(true);
    loadUsers();
  }, []);

  function loadUsers() {
    fetch('http://localhost:8080/api/users')
      .then((res) => res.json())
      .then((result) => {
        setIsLoading(false);
        setUsers(result);
      });
  }

  function isClear() {
    return !(
      selectedUser.identityNo === '' ||
      selectedUser.name === '' ||
      selectedUser.surname === '' ||
      selectedUser.gender === '' ||
      selectedUser.role === ''
    );
  }

  function clearForm() {
    setSelectedUser({
      identityNo: '',
      name: '',
      surname: '',
      gender: '',
      role: '',
    });
  }

  function handleInputChange(e) {
    const { name, value } = e.target;
    setSelectedUser({ ...selectedUser, [name]: value });
  }

  function deleteUser() {
    fetch(`http://localhost:8080/api/users/${selectedUser.id}`, {
      method: 'DELETE',
    }).then(() => {
      setShow(false);
      loadUsers();
      clearForm();
    });
  }

  function createUser() {
    fetch('http://localhost:8080/api/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      mode: 'cors',
      body: JSON.stringify(selectedUser),
    }).then(() => {
      setShow(false);
      loadUsers();
      clearForm();
    });
  }

  function updateUser() {
    fetch('http://localhost:8080/api/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      mode: 'cors',
      body: JSON.stringify(selectedUser),
    }).then(() => {
      setShow(false);
      loadUsers();
      clearForm();
    });
  }

  return (
    <>
      <Container>
        <Row>
          <Col sm={8}>
            {isLoading ? (
              <Spinner
                animation='border'
                role='status'
                variant='primary'
                className='text-center'
              >
                <span className='visually-hidden'>Loading...</span>
              </Spinner>
            ) : (
              <Table
                striped
                bordered
                hover
              >
                <thead>
                  <tr>
                    <th>Identity No</th>
                    <th> Name</th>
                    <th> Surname</th>
                    <th>Gender</th>
                    <th>Role</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((user) => (
                    <tr
                      key={user.id}
                      onClick={() => {
                        setSelectedUser(user);
                      }}
                    >
                      <td>{user.identityNo}</td>
                      <td>{user.name}</td>
                      <td>{user.surname}</td>
                      <td>{user.gender}</td>
                      <td>{user.role}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            )}
          </Col>
          <Col sm={4}>
            <Form>
              <Form.Group
                className='mb-3'
                controlId='identityNo'
              >
                <Form.Label>Identity No</Form.Label>
                <Form.Control
                  type='text'
                  placeholder='Identity No'
                  value={selectedUser.identityNo}
                  onChange={(e) => handleInputChange(e)}
                  name='identityNo'
                />
              </Form.Group>
              <Form.Group
                className='mb-3'
                controlId='name'
              >
                <Form.Label>Name</Form.Label>
                <Form.Control
                  type='text'
                  placeholder='Name'
                  name='name'
                  value={selectedUser.name}
                  onChange={(e) => handleInputChange(e)}
                />
              </Form.Group>
              <Form.Group
                className='mb-3'
                controlId='surname'
              >
                <Form.Label>Surname</Form.Label>
                <Form.Control
                  type='text'
                  placeholder='Surname'
                  name='surname'
                  value={selectedUser.surname}
                  onChange={(e) => handleInputChange(e)}
                />
              </Form.Group>
              <Form.Group
                className='mb-3'
                controlId='gender'
              >
                <Form.Label>Gender</Form.Label>
                <Form.Select
                  aria-label='Please select gender'
                  value={selectedUser.gender}
                  name='gender'
                  onChange={(e) => handleInputChange(e)}
                >
                  <option>Please select gender</option>
                  <option value='MALE'>Male</option>
                  <option value='FEMALE'>Female</option>
                </Form.Select>
              </Form.Group>
              <Form.Group
                className='mb-3'
                controlId='role'
              >
                <Form.Label>Role</Form.Label>
                <Form.Select
                  aria-label='Please select role'
                  value={selectedUser.role}
                  name='role'
                  onChange={(e) => handleInputChange(e)}
                >
                  <option>Please select role</option>
                  <option value='STUDENT'>Student</option>
                  <option value='TEACHER'>Teacher</option>
                </Form.Select>
              </Form.Group>
              {selectedUser.id ? (
                <Button
                  variant='primary'
                  type='button'
                  onClick={updateUser}
                >
                  Update User
                </Button>
              ) : (
                <Button
                  variant='primary'
                  type='button'
                  onClick={createUser}
                >
                  Create User
                </Button>
              )}{' '}
              {isClear() ? (
                <>
                  <Button
                    variant='outline-primary'
                    type='button'
                    onClick={() => clearForm()}
                  >
                    Clear
                  </Button>{' '}
                  <Button
                    variant='danger'
                    type='button'
                    onClick={handleShow}
                  >
                    Delete
                  </Button>
                </>
              ) : (
                ' '
              )}{' '}
            </Form>
          </Col>
        </Row>
        <Modal
          show={show}
          onHide={handleClose}
        >
          <Modal.Header closeButton>
            <Modal.Title>Delete</Modal.Title>
          </Modal.Header>
          <Modal.Body>Are you sure ?</Modal.Body>
          <Modal.Footer>
            <Button
              variant='secondary'
              onClick={handleClose}
            >
              Close
            </Button>
            <Button
              variant='danger'
              onClick={deleteUser}
            >
              Delete
            </Button>
          </Modal.Footer>
        </Modal>
      </Container>
    </>
  );
}
