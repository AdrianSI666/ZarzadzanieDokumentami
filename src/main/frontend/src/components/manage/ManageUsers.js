import axios from "axios";
import { Button, Modal, Form, Table, Alert } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import './ManageUsers.css';
import React, { useState, useEffect } from "react";
import { Pagination } from "@material-ui/lab";
import inMemoryJWTMenager from "../../services/inMemoryJWTMenager";
import jwtDecode from "jwt-decode";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { loggOfUser } from '../redux/config';
const ManageUsers = () => {
  const config = useSelector((state) => state.config)
  const localHost = config.localHost
  const port = config.port
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [message, setMessage] = useState("");
  const [accessMessage, setAccessMessage] = useState("");
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [pageSize, setPageSize] = useState(5);

  const [userInfos, setUserInfos] = useState([]);
  //const [roles, setRoles] = useState([]);

  const [name, setName] = useState("")
  const [surname, setSurname] = useState("")
  const [userId, setUserId] = useState(-1)

  const [newName, setNewName] = useState("")
  const [newSurname, setNewSurname] = useState("")
  const [newEmail, setNewEmail] = useState("")
  const [newPassword, setNewPassword] = useState("")

  const [userEditModal, setUserEditModal] = useState(false);
  const [userAddModal, setUserAddModal] = useState(false);
  const [deleteConfirmModal, setDeleteConfirmModal] = useState(false)

  axios.defaults.headers.common["Authorization"] = `Bearer ${inMemoryJWTMenager.getToken()}`;

  axios.interceptors.request.use(async config => {
    if (inMemoryJWTMenager.getToken() != null && inMemoryJWTMenager.getRefreshToken() != null) {
      let decodedToken = jwtDecode(inMemoryJWTMenager.getToken())
      let dateNow = new Date().valueOf() / 1000;
      if (decodedToken.exp < dateNow) {
        const uninterceptedAxiosInstance = axios.create();
        await uninterceptedAxiosInstance({
          url: `http://${localHost}:${port}/token/refresh`,
          method: "GET",
          headers: {
            Authorization: `Bearer ${inMemoryJWTMenager.getToken()}`,
            Refresh: inMemoryJWTMenager.getRefreshToken()
          }
        }).then(res => {
          inMemoryJWTMenager.setToken(res.data.access_token)
          config.headers.authorization = `Bearer ${res.data.access_token}`;
          inMemoryJWTMenager.setRefreshToken(res.data.refresh_token)
        }).catch(() => {
          setAccessMessage("Nie można potwierdzić twojej tożsamości. Zaloguj się ponownie.")
          inMemoryJWTMenager.deleteToken()
          dispatch(loggOfUser())
        })
      }
    } else if (inMemoryJWTMenager.getToken() == null && inMemoryJWTMenager.getRefreshToken() != null) {
      let decodedToken = jwtDecode(inMemoryJWTMenager.getRefreshToken())
      let dateNow = new Date().valueOf() / 1000;
      if (decodedToken.exp > dateNow) {
        const uninterceptedAxiosInstance = axios.create();
        await uninterceptedAxiosInstance({
          url: `http://${localHost}:${port}/token/refresh`,
          method: "GET",
          headers: {
            Authorization: `Bearer ${inMemoryJWTMenager.getRefreshToken()}`,
            Refresh: inMemoryJWTMenager.getRefreshToken()
          }
        }).then(res => {
          inMemoryJWTMenager.setToken(res.data.access_token)
          config.headers.authorization = `Bearer ${res.data.access_token}`;
          inMemoryJWTMenager.setRefreshToken(res.data.refresh_token)
        }).catch(() => {
          setAccessMessage("Nie można potwierdzić twojej tożsamości. Zaloguj się ponownie.")
          inMemoryJWTMenager.deleteToken()
          dispatch(loggOfUser())
        })
      } else {
        setAccessMessage("Sesja wygasła, zaloguj się ponownie")
        inMemoryJWTMenager.deleteToken()
        dispatch(loggOfUser())
      }
    } else if (inMemoryJWTMenager.getToken() == null && inMemoryJWTMenager.getRefreshToken() == null) {
      inMemoryJWTMenager.deleteToken()
      dispatch(loggOfUser())
      navigate("/login")
    }
    return config;
  });

  function getUsers(page, pageSize) {
    let params = {
      page,
      'page_size': pageSize
    }
    axios.get(`http://${localHost}:${port}/users`,
      {
        params: params,
        paramsSerializer: {
          indexes: null
        }
      })
      .then(res => {
        setUserInfos(res.data.data)
        setPage(res.data.currentPage)
        setTotalPages(res.data.totalPages)
      }).catch(() => {
        setMessage("Wystąpił błąd podczas pobierania użytkowników z bazy.")
        setTimeout(function () {
          setMessage(undefined)
        }, 4000);
      })
  }

  // When you want to add option to set role to user while creating use this.
  // useEffect(() => {
  //   let params = {
  //     page,
  //     'page_size': 100
  //   }
  //   axios.get(`http://${localHost}:${port}/roles`, {
  //     params: params,
  //     paramsSerializer: {
  //       indexes: null
  //     }
  //   })
  //     .then(res => {
  //       setRoles(res.data)
  //     }).catch((err) => {
  //       if (err.response.status != 403) setMessage("Nie wszystkie wymagane dane z bazy danych udało się pobrać. Spróbuj ponownie.")
  //     })
  // }, [])

  useEffect(() => {
    getUsers(page, pageSize)
  }, [page]);
  function addUser(e, name, surname, email, password){
    e.preventDefault()
    const newUserDTO = {
      "name": name,
      "surname": surname,
      "email": email,
      "password": password
    }
    axios.post(`http://${localHost}:${port}/users/`, newUserDTO)
    .then((res) => {
      setUserInfos([...userInfos, res.data])
    }).catch(() => {
      setMessage("Wystąpił błąd podczas dodawania nowego użytkownika z imieniem: " + name)
    })
  }

  function editUser(e, name, surname) {
    e.preventDefault()
    const userDTO = {
      "name": name,
      "surname": surname
    }
    axios.put(`http://${localHost}:${port}/users/${userId}`, userDTO)
      .then((res) => {
        const changedUser = res.data
        userInfos.forEach(user => {
          if (user.id === userId) {
            user.title = changedUser.name
            user.date = changedUser.surname
          }
        })
        setUserInfos([...userInfos])
      }).catch(() => {
        setMessage("Wystąpił błąd podczas zapisywania zmian w użytkowniku: " + name)
      })
  }

  function deleteUser(e, id) {
    e.preventDefault()
    axios.delete(`http://${localHost}:${port}/users/${id}`).then(() => {
      setMessage("Pomyślnie usunięto użytkownika")
      getUsers(page, pageSize)
    }).catch(() => {
      setMessage("Wystąpił błąd podczas usuwania użytkownika")
    })
    setTimeout(function () {
      setMessage(undefined)
    }, 4000);
  }

  const handlePageChange = (event, value) => {
    setPage(value)
  };

  return (
    <div>
      <Button variant='success' onClick={() => { setUserAddModal(true); }}>
        Dodaj użytkownika
      </Button>

      {accessMessage && (<Alert key={"danger"} variant={"danger"}>
        {accessMessage}
      </Alert>
      )}
      {message && (<Alert key={"warning"} variant={"warning"}>
        {message}
      </Alert>
      )}
      <div className="card">
        <div className="card-header">
          Lista użytkowników
        </div>
        <div className="card-header">
          <span>Ilość wyświetlanych użytkowników na stronę:</span>
          <Form.Select className="ms-2 w-6 d-inline" onChange={e => {
            let newPage = Math.ceil((page * pageSize - pageSize + 1) / e.target.value)
            getUsers(newPage, e.target.value)
            setPageSize(e.target.value)
          }} defaultValue={5}>
            <option value="5" key={5}>5</option>
            <option value="10" key={10}>10</option>
            <option value="20" key={20}>20</option>
            <option value="40" key={40}>40</option>
            <option value="80" key={80}>80</option>
          </Form.Select>
        </div>
        <Table bordered hover>
          <thead>
            <tr>
              <th className="th-ls">Imie</th>
              <th className="th-ms">Nazwisko</th>
              <th className="th-ss">Email</th>
              <th className="th-ss">Role</th>
              <th colSpan={2}>Akcje</th>
            </tr>
          </thead>
          <tbody>
            {userInfos.length > 0 && userInfos.map((user) => (
              <tr key={user.id}>
                <td>{user.name}</td>
                <td>{user.surname}</td>
                <td>{user.email}</td>
                <td>{user.roles}</td>
                <td>
                  <Button variant="success" onClick={() => {
                    setUserId(user.id)
                    setName(user.name)
                    setSurname(user.surname)
                    setUserEditModal(true)
                  }}>
                    Edytuj
                  </Button>
                </td>
                <td>
                  <Button variant="danger" onClick={() => { 
                    setUserId(user.id)
                    setName(user.name)
                    setDeleteConfirmModal(true); 
                    }}>
                    Usuń
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      </div>
      <div className="d-flex justify-content-center">
        <Pagination
          className="my-3"
          color="primary"
          showFirstButton
          showLastButton
          count={totalPages}
          page={page}
          siblingCount={3}
          boundaryCount={2}
          variant="outlined"
          shape="rounded"
          onChange={handlePageChange}
        />
      </div>

      <Modal
        show={userEditModal}
        onHide={() => {
          setUserEditModal(false)
          setUserId(-1)
        }}
        size="lg"
        aria-labelledby="contained-modal-title-vcenter"
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title id="contained-modal-title-vcenter">
            Edycja informacji o użytkowniku
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={(e) => {
            editUser(e, name, surname);
            setUserEditModal(false);
          }}>
            <Form.Group className="mb-3" controlId="formTitle">
              <Form.Label>Imie</Form.Label>
              <Form.Control value={name} type="text" onChange={e => setName(e.target.value)} />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formTitle">
              <Form.Label>Nazwisko</Form.Label>
              <Form.Control value={surname} type="text" onChange={e => setSurname(e.target.value)} />
            </Form.Group>

            <Button variant="primary" type="submit">
              Zmień
            </Button>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="danger" onClick={(e) => {
            e.preventDefault()
            setUserEditModal(false)
            setUserId(-1)
          }}>Zamknij</Button>
        </Modal.Footer>
      </Modal>

      <Modal
        show={userAddModal}
        onHide={() => setUserAddModal(false)}
        size="lg"
        aria-labelledby="contained-modal-title-vcenter"
        centered>
        <Modal.Header closeButton>
          <Modal.Title id="contained-modal-title-vcenter">
            Dodanie nowego użytkownika
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={(e) => {
            addUser(e, newName, newSurname, newEmail, newPassword);
            setUserAddModal(false);
          }}>
            <Form.Group className="mb-3" controlId="formTitle">
              <Form.Label>Imie</Form.Label>
              <Form.Control value={newName} type="text" onChange={e => setNewName(e.target.value)} />
            </Form.Group>
            <Form.Group className="mb-3" controlId="formTitle">
              <Form.Label>Nazwisko</Form.Label>
              <Form.Control value={newSurname} type="text" onChange={e => setNewSurname(e.target.value)} />
            </Form.Group>
            <Form.Group className="mb-3" controlId="formTitle">
              <Form.Label>Email</Form.Label>
              <Form.Control value={newEmail} type="email" onChange={e => setNewEmail(e.target.value)} />
            </Form.Group>
            <Form.Group className="mb-3" controlId="formTitle">
              <Form.Label>Hasło</Form.Label>
              <Form.Control value={newPassword} type="password" onChange={e => setNewPassword(e.target.value)} />
            </Form.Group>
            <Button variant="primary" type="submit">
              Dodaj nowego użytkownika
            </Button>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="danger" onClick={(e) => {
            e.preventDefault()
            setUserAddModal(false)
          }
          }>Zamknij</Button>
        </Modal.Footer>
      </Modal>

      <Modal
        show={deleteConfirmModal}
        onHide={() => setDeleteConfirmModal(false)}
        size="lg"
        aria-labelledby="contained-modal-title-vcenter"
        centered>
        <Modal.Header closeButton>
          <Modal.Title id="contained-modal-title-vcenter">
            Potwierdzenie usunięcia
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={(e) => {
            deleteUser(e, userId);
            setDeleteConfirmModal(false);
          }}>
            <Form.Group className="mb-3" controlId="formTitle">
              <Form.Label>Czy napewno chcesz usunąć dany dokument z nazwą: {name}</Form.Label>
              <Button className='ms-5' variant="danger" type="submit">
                Usuń
              </Button>
              <Button className='ms-5' variant="primary" onClick={(e) => {
                e.preventDefault()
                setDeleteConfirmModal(false)
              }
              }>Cofnij</Button>
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="danger" onClick={(e) => {
            e.preventDefault()
            setDeleteConfirmModal(false)
          }
          }>Zamknij</Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default ManageUsers;
