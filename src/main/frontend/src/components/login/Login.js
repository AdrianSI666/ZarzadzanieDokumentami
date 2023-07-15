import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, useOutletContext } from 'react-router-dom';
import inMemoryJWTMenager from '../../services/inMemoryJWTMenager';

import 'bootstrap/dist/css/bootstrap.min.css';
import { Button, Container, Form, Modal } from 'react-bootstrap';

const LoginProfile = () => {
    const [address] = useOutletContext();
    const localHost=address.localHost;
    const port=address.port;
    const navigate = useNavigate();
      const [name, setName] = useState("");
      const [surname, setSurname] = useState("");
      const [email, setEmail] = useState("");
      const [password, setPassword] = useState("");
      const [modalAddUser, setModalAddUser] = React.useState(false);
      const [loginError, setLoginError] = useState(null);
      const [addError, setAddError] = useState(null);
      function login(e, email, password) {
        e.preventDefault()
        if(email != "" || password != ""){
          let authorities = {
            email: email,
            password: password
          }
          axios.post(`http://${localHost}:${port}/login`, authorities)
            .then((res) => {
              inMemoryJWTMenager.setToken(res.data.access_token)
              inMemoryJWTMenager.setRefreshToken(res.data.refresh_token);
              navigate('/user/files', { state: { id: res.data.user_id } })
            })
            .catch(() => {
              setPassword("")
              setLoginError(true)
            })
        } else {
          setLoginError(true)
        }
      }
    function createAccount(e, name, surname, email, password) {
        e.preventDefault()
        const newUser = {
          name,
          surname,
          email,
          password
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
        {loginError===true && <span>Złe dane logowania</span>}
        <Form onSubmit={(e) => {
          login(e, email, password);
        }}>
          <Button variant='info' onClick={ () => setModalAddUser(true)}>
            Create account
          </Button>
          <Form.Group className='mb-3' controlId='formBasicText'>
            <Form.Label>Email</Form.Label>
            <Form.Control className='mb-3' value={email} type="email" placeholder='Podaj email' onChange={e => setEmail(e.target.value)} />
            <Form.Label>Hasło</Form.Label>
            <Form.Control className='mb-3' value={password} type="password" onChange={e => setPassword(e.target.value)} />
          </Form.Group>
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
              createAccount(e, name, surname, email, password);
            }}>
              {addError && <span>User with given name and surname already exists chose different name and surname</span>}
              <Form.Group className='mb-3' controlId='formBasicText'>
                <Form.Label>Imie</Form.Label>
                <Form.Control value={name} type='text' placeholder='Podaj imie'
                  onChange={e => setName(e.target.value)} />
              </Form.Group>
              <Form.Group className='mb-3' controlId='formBasicText'>
                <Form.Label>Nazwisko</Form.Label>
                <Form.Control value={surname} type='text' placeholder='Podaj nazwisko'
                  onChange={e => setSurname(e.target.value)} />
              </Form.Group>
              <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>email</Form.Label>
                <Form.Control value={email} type="text" placeholder='Podaj email' onChange={e => setEmail(e.target.value)} />
              </Form.Group>
              <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Hasło</Form.Label>
                <Form.Control value={password} type="text" placeholder='' onChange={e => setPassword(e.target.value)} />
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
  