import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';

export default function Lecture() {
  return (
    <>
      <Container>
        <Row>
          <Col sm={6}>
            <Table
              striped
              bordered
              hover
            >
              <thead>
                <tr>
                  <th>Id</th>
                  <th>Lecture</th>
                  <th>Teacher</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>1</td>
                  <td>Math</td>
                  <td>Samet AKBAL</td>
                </tr>
              </tbody>
            </Table>
          </Col>
          <Col sm={3}>
            <Form>
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
                controlId='teacher'
              >
                <Form.Label>Role</Form.Label>
                <Form.Select aria-label='Please select teacher'>
                  <option>Please select teacher</option>
                  <option value='1'>Samet Akbal</option>
                  <option value='1'>Test Akbal</option>
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
          <Col sm={3}>
            <Form>
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
