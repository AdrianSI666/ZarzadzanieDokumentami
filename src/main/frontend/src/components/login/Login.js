import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import inMemoryJWTMenager from '../../services/inMemoryJWTMenager';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Alert, Button, Container, Form } from 'react-bootstrap';
import { useDispatch, useSelector } from 'react-redux';
import { loggInMannager, loggInUser } from '../redux/config';
import jwtDecode from 'jwt-decode';

const LoginProfile = () => {
    const config = useSelector((state) => state.config)
    const dispatch = useDispatch();
    const localHost=config.localHost;
    const port=config.port;
    
    const navigate = useNavigate();
      const [email, setEmail] = useState("");
      const [password, setPassword] = useState("");
      const [loginError, setLoginError] = useState(null);
      function login(e, email, password) {
        e.preventDefault()
        if(email != "" || password != ""){
          let authorities = {
            email: email,
            password: password
          }
          axios.post(`http://${localHost}:${port}/login`, authorities)
            .then((res) => {
              dispatch(loggInUser(res.data.user_id))
              if(jwtDecode(res.data.access_token).roles.includes("Manager"))
              {
                dispatch(loggInMannager(true))
              }
              inMemoryJWTMenager.setToken(res.data.access_token)
              inMemoryJWTMenager.setRefreshToken(res.data.refresh_token);
              navigate('/user/files')
            })
            .catch(() => {
              setPassword("")
              setLoginError(true)
            })
        } else {
          setLoginError(true)
        }
      }
  
    return (
      <Container>
        {loginError===true && (<Alert key={"danger"} variant={"danger"}>
          Niepoprawne dane logowania
        </Alert>)}
        <Form onSubmit={(e) => {
          login(e, email, password);
        }}>
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
  