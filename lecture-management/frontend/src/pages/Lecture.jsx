import { useEffect, useState } from "react";
import { Container, Row, Col, Table, Pagination, Form, Button, Alert, Modal, ListGroup } from "react-bootstrap";

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
        students: []
    });

    useEffect(() => {
        loadLectures();
        loadTeachers();
    }, [currentPage]);

    function loadLectureStudents(lecture) {
        fetch('http://localhost:8080/api/users/potential-students', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            mode: 'cors',
            body: JSON.stringify(lecture.students.map((st) => st.id))
        }).then((res) => res.json())
            .then((result) => {
                console.log(result);
                setLectureStudents(result);
            });
    }


    function loadLectures() {
        fetch(`http://localhost:8080/api/lectures?page=${currentPage - 1}`)
            .then(res => res.json())
            .then((result) => {
                setLectures(result.content);
                let items = [];
                for (let index = 1; index <= result.totalPages; index++) {
                    items.push(
                        <Pagination.Item key={index} active={currentPage === index} onClick={() => setCurrentPage(index)} >
                            {index}
                        </Pagination.Item>
                    );
                    setPageItems(items);
                }
            });
    }

    function loadTeachers() {
        fetch(`http://localhost:8080/api/users/by-role?role=TEACHER`)
            .then(res => res.json())
            .then((result) => {
                setTeachers(result);
            });
    }


    function handleInputChange(e) {
        const { name, value } = e.target;
        setSelectedLecture({ ...selectedLecture, [name]: value });
        setLectureStudents([]);
    }

    function createLecture() {
        const lecture = {
            name: selectedLecture.name,
            teacher: {
                id: Number(selectedLecture.teacherId)
            }
        }

        fetch('http://localhost:8080/api/lectures', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            mode: 'cors',
            body: JSON.stringify(lecture)
        }).then((res) => res.json())
            .then((result) => {
                loadLectures();
                clearForm();
            });

    }

    function clearForm() {
        setSelectedLecture({
            name: '',
            teacherId: 0,
            teacher: {},
            students: []
        });

        setLectureStudents([]);

    }

    function addStudent(student) {
        selectedLecture.students.push(student);

        fetch('http://localhost:8080/api/lectures', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            mode: 'cors',
            body: JSON.stringify(selectedLecture)
        }).then((res) => res.json())
            .then((result) => {
                loadLectures();
                clearForm();
            });
    }

    function setLecture(lecture) {
        if (lecture.id === selectedLecture.id) {
            clearForm();
        } else {
            lecture.teacherId = lecture.teacher.id;
            setSelectedLecture(lecture);
            loadLectureStudents(lecture);
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
            body: JSON.stringify(selectedLecture)
        }).then((res) => res.json())
            .then((result) => {
                loadLectures();
                clearForm();
            });

    }

    return <>
        <Container>
            <Row>
                <Col sm={9}>
                    <Table striped bordered hover>
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
                                                    <Button size="sm" variant="danger" onClick={() => { removeStudent(student.id) }}>
                                                        Remove
                                                    </Button>
                                                </td>
                                            </tr>
                                        )) : ''}
                                </>
                            ))}
                        </tbody>
                    </Table>
                    <Pagination>{pageItems}</Pagination>
                </Col>
                <Col sm={3}>
                    <Form>
                        <Form.Group className="mb-3" controlId="name">
                            <Form.Label>Name</Form.Label>
                            <Form.Control
                                type='text'
                                autoComplete='off'
                                placeholder='Name'
                                name='name'
                                maxLength={'11'}
                                value={selectedLecture.name}
                                onChange={(e) => handleInputChange(e)}
                            />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="teacherId">
                            <Form.Label>Teacher</Form.Label>
                            <Form.Select
                                aria-label="Please select teacher"
                                name="teacherId"
                                value={Number(selectedLecture.teacherId)}
                                onChange={(e) => handleInputChange(e)}
                            >
                                <option>Please select teacher</option>
                                {teachers.map((teacher) => (
                                    <option value={teacher.id} key={teacher.id}>
                                        {teacher.name + ' ' + teacher.surname}
                                    </option>
                                ))}
                            </Form.Select>
                        </Form.Group>
                        <Button variant="primary" type="button" onClick={createLecture}>
                            Create
                        </Button>{' '}
                        {selectedLecture.name !== '' ?
                            <Button variant="outline-primary" type="button" onClick={clearForm}>
                                Clear
                            </Button> : ''

                        }
                    </Form><br />
                    <ListGroup as='ol' numbered>
                        {lectureStudents.map((student) => (
                            <ListGroup.Item as='li' className="d-flex justify-content-between align-items-start" key={student.id}>
                                <div className="ms-2 me-auto">
                                    {student.name} {student.surname}
                                </div>
                                <Button variant="outline-primary" size='sm' onClick={() => addStudent(student)}>
                                    Add
                                </Button>
                            </ListGroup.Item>
                        ))}
                    </ListGroup>
                </Col>
            </Row>
        </Container>
    </>;
}