import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { useEffect, useState } from 'react';
import { Pagination } from 'react-bootstrap';
import Badge from 'react-bootstrap/Badge';
import ListGroup from 'react-bootstrap/ListGroup';

export default function Lecture() {
  const [lectures, setLectures] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageItems, setPageItems] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [lectureStudents, setLectureStudents] = useState([]);
  const [selectedLecture, setSelectedLecture] = useState({
    name: '',
    teacherId: 0,
    teacher: {},
    students: [],
  });

  useEffect(() => {
    loadLectures();
    loadTeachers();
  }, [currentPage]);

  function loadLectures() {
    fetch(
      `http://localhost:8080/api/lectures?page=${currentPage - 1}&pageSize=12`
    )
      .then((res) => res.json())
      .then((result) => {
        setLectures(result.content);
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

  function createLecture() {
    const lecture = {
      name: selectedLecture.name,
      teacher: {
        id: Number(selectedLecture.teacherId),
      },
    };
    console.log(lecture);

    fetch('http://localhost:8080/api/lectures', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      mode: 'cors',
      body: JSON.stringify(lecture),
    })
      .then((res) => res.json())
      .then((result) => {
        loadLectures();
        clearForm();
      })
      .catch((error) => {
        alert(error);
      });
  }

  function loadTeachers() {
    fetch(`http://localhost:8080/api/users/by-role?role=TEACHER`)
      .then((res) => res.json())
      .then((result) => {
        setTeachers(result);
      });
  }

  function setLecture(lecture) {
    if (lecture.id === selectedLecture.id) {
      clearForm();
    } else {
      lecture.teacherId = lecture.teacher.id;
      setSelectedLecture(lecture);
      loadLectureStudents();
    }
  }

  function removeStudent(studentId) {
    selectedLecture.students = selectedLecture.students.filter(
      (student) => student.id !== studentId
    );
    fetch('http://localhost:8080/api/lectures', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      mode: 'cors',
      body: JSON.stringify(selectedLecture),
    })
      .then((res) => res.json())
      .then((result) => {
        loadLectures();
        clearForm();
      })
      .catch((error) => {
        alert(error);
      });
  }

  function handleInputChange(e) {
    const { name, value } = e.target;
    setSelectedLecture({ ...selectedLecture, [name]: value });
  }

  function clearForm() {
    setSelectedLecture({ name: '', teacherId: 0, students: [] });
    setLectureStudents([]);
  }

  function loadLectureStudents() {
    fetch(`http://localhost:8080/api/users/potential-students`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      mode: 'cors',
      body: JSON.stringify(
        selectedLecture.students.map((student) => student.id)
      ),
    })
      .then((res) => res.json())
      .then((result) => {
        setLectureStudents(result);
      });
  }

  function addStudent(student) {
    selectedLecture.students.push(student);
    fetch('http://localhost:8080/api/lectures', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      mode: 'cors',
      body: JSON.stringify(selectedLecture),
    })
      .then((res) => res.json())
      .then((result) => {
        loadLectures();
        clearForm();
      })
      .catch((error) => {
        alert(error);
      });
  }

  return (
    <>
      <Container>
        <Row>
          <Col sm={9}>
            <Table
              striped
              bordered
              hover
            >
              <thead>
                <tr>
                  <th>Id</th>
                  <th>Name</th>
                  <th>Teacher</th>
                </tr>
              </thead>
              <tbody>
                {lectures.map((lec) => (
                  <>
                    <tr
                      key={lec.id}
                      onClick={() => {
                        setLecture(lec);
                      }}
                    >
                      <td>{lec.id}</td>
                      <td>{lec.name}</td>
                      <td>{lec.teacher.name + ' ' + lec.teacher.surname}</td>
                    </tr>
                    {selectedLecture.id && lec.id === selectedLecture.id
                      ? selectedLecture.students.map((student) => (
                          <tr key={student.identityNo}>
                            <td></td>
                            <td>{student.identityNo}</td>
                            <td>{student.name + ' ' + student.surname}</td>
                            <td>
                              <Button
                                size='sm'
                                variant='danger'
                                onClick={() => removeStudent(student.id)}
                              >
                                Remove
                              </Button>
                            </td>
                          </tr>
                        ))
                      : ''}
                  </>
                ))}
              </tbody>
            </Table>
            <Pagination>{pageItems}</Pagination>
          </Col>
          <Col sm={3}>
            <Form>
              <Form.Group
                className='mb-3'
                controlId='name'
              >
                <Form.Label>Name</Form.Label>
                <Form.Control
                  autoComplete='off'
                  type='text'
                  placeholder='Name'
                  name='name'
                  value={selectedLecture.name}
                  onChange={(e) => handleInputChange(e)}
                />
              </Form.Group>
              <Form.Group
                className='mb-3'
                controlId='teacherId'
              >
                <Form.Label>Teacher</Form.Label>
                <Form.Select
                  aria-label='Please select teacher'
                  name='teacherId'
                  value={Number(selectedLecture.teacherId)}
                  onChange={(e) => handleInputChange(e)}
                >
                  <option>Please select teacher</option>
                  {teachers.map((teacher) => (
                    <option value={teacher.id}>
                      {teacher.name} {teacher.surname}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
              <Button
                variant='primary'
                type='button'
                onClick={createLecture}
              >
                Submit
              </Button>{' '}
              {selectedLecture.name !== '' ? (
                <>
                  <Button
                    variant='outline-primary'
                    onClick={clearForm}
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
                </>
              ) : (
                ''
              )}
            </Form>
            <br />
            <ListGroup
              as='ol'
              numbered
            >
              {lectureStudents.map((st) => (
                <ListGroup.Item
                  as='li'
                  className='d-flex justify-content-between align-items-start'
                >
                  <div className='ms-2 me-auto'>
                    {st.name} {st.surname}
                  </div>
                  <Button
                    variant='outline-primary'
                    size='sm'
                    onClick={() => addStudent(st)}
                  >
                    Add
                  </Button>
                </ListGroup.Item>
              ))}
            </ListGroup>
          </Col>
        </Row>
      </Container>
    </>
  );
}
