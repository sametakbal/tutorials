import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';

export default function User() {
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
                <tr>
                  <td>11231312</td>
                  <td>Samet</td>
                  <td>AKBAL</td>
                  <td>MALE</td>
                  <td>STUDENT</td>
                </tr>
              </tbody>
            </Table>
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
                />
              </Form.Group>
              <Form.Group
                className='mb-3'
                controlId='gender'
              >
                <Form.Label>Gender</Form.Label>
                <Form.Select aria-label='Please select gender'>
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
                <Form.Select aria-label='Please select role'>
                  <option>Please select role</option>
                  <option value='STUDENT'>Student</option>
                  <option value='TEACHER'>Teacher</option>
                </Form.Select>
              </Form.Group>
              <Button
                variant='primary'
                type='submit'
              >
                Submit
              </Button>{' '}
              <Button
                variant='outline-primary'
                type='button'
              >
                Clear
              </Button>{' '}
              <Button
                variant='danger'
                type='button'
              >
                Delete
              </Button>
            </Form>
          </Col>
        </Row>
      </Container>
    </>
  );
}
