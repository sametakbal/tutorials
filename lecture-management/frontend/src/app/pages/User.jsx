import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { useEffect, useState } from 'react';
import { Modal } from 'react-bootstrap';
import Pagination from 'react-bootstrap/Pagination';
import Alert from 'react-bootstrap/Alert';

export default function User() {
  const [errorMessage, setErrorMessage] = useState(null);
  const [users, setUsers] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageItems, setPageItems] = useState([]);

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
    loadUsers();
  }, [currentPage]);

  function loadUsers() {
    fetch(`http://localhost:8080/api/users?page=${currentPage - 1}`)
      .then((res) => res.json())
      .then((result) => {
        setUsers(result.content);
        let items = [];
        for (let number = 1; number <= result.totalPages; number++) {
          items.push(
            <Pagination.Item
              key={number}
              active={number === currentPage}
              onClick={() => setCurrentPage(number)}
            >
              {number}
            </Pagination.Item>
          );
        }
        setPageItems(items);
      });
  }

  function isClear() {
    return (
      selectedUser.identityNo !== '' ||
      selectedUser.name !== '' ||
      selectedUser.surname !== '' ||
      selectedUser.gender !== '' ||
      selectedUser.role !== ''
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

  function saveUser() {
    fetch('http://localhost:8080/api/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      mode: 'cors',
      body: JSON.stringify(selectedUser),
    })
      .then((res) => res.json())
      .then((result) => {
        setShow(false);
        loadUsers();
        setErrorMessage(result.errorMessage);
        clearForm();
      })
      .catch((error) => {
        alert(error);
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
            <Pagination>{pageItems}</Pagination>
          </Col>
          <Col sm={4}>
            <Form>
              {errorMessage ? (
                <Alert
                  key='danger'
                  variant='danger'
                >
                  {errorMessage}
                </Alert>
              ) : (
                ''
              )}
              <Form.Group
                className='mb-3'
                controlId='identityNo'
              >
                <Form.Label>Identity No</Form.Label>
                <Form.Control
                  type='text'
                  autoComplete='off'
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
                  autoComplete='off'
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
                  autoComplete='off'
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
                  onClick={saveUser}
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
                  {selectedUser.id ? (
                    <Button
                      variant='danger'
                      type='button'
                      onClick={handleShow}
                    >
                      Delete
                    </Button>
                  ) : (
                    ''
                  )}
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
