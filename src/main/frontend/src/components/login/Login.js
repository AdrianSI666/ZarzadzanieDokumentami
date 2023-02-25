import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import { Button, Container, Form, Modal } from 'react-bootstrap';

const LoginProfile = () => {
    const localHost = "localhost";
    const port = "8091";
    const navigate = useNavigate();
      const [name, setName] = useState("");
      const [surname, setSurname] = useState("");
      const [email, setEmail] = useState("");
      const [modalAddUser, setModalAddUser] = React.useState(false);
      const [loginError, setLoginError] = useState(null);
      const [addError, setAddError] = useState(null);
      function login(e, name) {
        e.preventDefault()
        axios.get(`http://${localHost}:${port}/users/name/${name}`)
          .then((res) => {
            navigate(`/files/${res.data.id}`)
          })
          .catch(() => {
            setLoginError(true)
          })
      }
    function createAccount(e, name, surname, email) {
        e.preventDefault()
        const newUser = {
          name,
          surname,
          email
        }
        axios.post(`http://${localHost}:${port}/users`, newUser)
          .then((res) => {
            navigate(`/files/${res.data.id}`)
          })
          .catch(() => {
            setAddError(true)
          })
      }
  
    return (
      <Container>
        {loginError===true && <span>Wrong login data</span>}
        <Form onSubmit={(e) => {
          login(e, name);
        }}>
          <Button variant='info' onClick={ () => setModalAddUser(true)}>
            Create account
          </Button>
          <Form.Group className='mb-3' controlId='formBasicText'>
            <Form.Label>Name</Form.Label>
            <Form.Control value={name} type="text" placeholder='Provide name' onChange={e => setName(e.target.value)} />
          </Form.Group>
          {/* <Form.Group className='mb-3' controlId='formBasicText'>
            <Form.Label>Surname</Form.Label>
            <Form.Control value={surname} type="text" placeholder='Provide surname' onChange={e => setSurname(e.target.value)} />
          </Form.Group>
          <Form.Group className='mb-3' controlId='formBasicText'>
            <Form.Label>Email</Form.Label>
            <Form.Control value={email} type="text" placeholder='Provide email' onChange={e => setEmail(e.target.value)} />
          </Form.Group> */}
          <Button variant='success' type='submit'>
            Login
          </Button>
        </Form>
        <Modal
          show={modalAddUser}
          onHide={() => setModalAddUser(false)}
          size="lg"
          aria-labelledby="contained-modal-title-vcenter"
          centered
        >
          <Modal.Header>
            <Modal.Title id="contained-modal-title-vcenter">
              Create a question
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form onSubmit={(e) => {
              createAccount(e, name, surname, email);
            }}>
              {addError && <span>User with given name and surname already exists chose different name and surname</span>}
              <Form.Group className='mb-3' controlId='formBasicText'>
                <Form.Label>Name</Form.Label>
                <Form.Control value={name} type='text' placeholder='Provide name'
                  onChange={e => setName(e.target.value)} />
              </Form.Group>
              <Form.Group className='mb-3' controlId='formBasicText'>
                <Form.Label>Surname</Form.Label>
                <Form.Control value={surname} type='text' placeholder='Provide surname'
                  onChange={e => setSurname(e.target.value)} />
              </Form.Group>
              <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>email</Form.Label>
                <Form.Control value={email} type="text" placeholder='Provide email' onChange={e => setEmail(e.target.value)} />
              </Form.Group>
              <Button variant='primary' type='submit'>
                Add
              </Button>
            </Form>
          </Modal.Body>
          <Modal.Footer>
            <Button onClick={() => setModalAddUser(false)}>Close</Button>
          </Modal.Footer>
        </Modal>
      </Container>
    )
  }
  
  function Login() {
    return (
      <div className='Login'>
        <LoginProfile />
      </div>
    )
  }
  
  export default Login;
  