import React, { useEffect, useState } from 'react'
import FullCalendar from '@fullcalendar/react'
import dayGridPlugin from '@fullcalendar/daygrid'
import multiMonthPlugin from '@fullcalendar/multimonth'
import allLocales from '@fullcalendar/core/locales-all'
import interactionPlugin from "@fullcalendar/interaction"
import axios from 'axios'
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { loggOfUser } from '../redux/config';
import inMemoryJWTMenager from '../../services/inMemoryJWTMenager'
import jwtDecode from 'jwt-decode'
import { Alert, Button, Form, Modal } from 'react-bootstrap'

import "./FullCalendar.css"
import { Divider, Drawer } from '@material-ui/core'

const Calendar = () => {
    const config = useSelector((state) => state.config)
    const localHost = config.localHost
    const port = config.port
    const id = config.user
    const dispatch = useDispatch();
    const navigate = useNavigate();
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

    const [accessMessage, setAccessMessage] = useState()
    const [message, setMessage] = useState("")

    const [events, setEvents] = useState([])
    const [dateFromTo, setDateFromTo] = useState()
    const [eventsWithoutDate, setEventsWithoutDate] = useState([])

    const [typesOfDocuments, setTypesOfDocuments] = useState([])
    const [newTypeName, setNewTypeName] = useState("")

    const [fileId, setFileId] = useState(-1)
    const [title, setTitle] = useState("")
    const [cost, setCost] = useState(-1)
    const [paid, setPaid] = useState(false)
    const [dateToPay, setDateToPay] = useState(new Date())
    const [typeId, setTypeId] = useState("")

    const [modalEventDetails, setModalEventDetails] = useState(false)
    const [addNewTypeModalShow, setAddNewTypeModalShow] = useState(false)
    const [deleteConfirmModal, setDeleteConfirmModal] = useState(false)
    
    useEffect(() => {
        axios.get(`http://${localHost}:${port}/types`)
            .then(res => {
                setTypesOfDocuments(res.data)
            }).catch((err) => {
                if (err.response.status != 403) setMessage("Nie wszystkie wymagane dane z bazy danych udało się pobrać. Spróbuj ponownie.")
            })
    }, [])

    async function handleDatesSet(data) {
        setDateFromTo(data)
        let range = [new Date(data.start), new Date(data.end)]
        let params = {
            "page_size": 2000,
            "filter_owner_id": id,
            "filter_date": range
        }
        axios.get(`http://${localHost}:${port}/documents/by`,
            {
                params: params,
                paramsSerializer: {
                    indexes: null
                }
            }
        ).then(res => {
            let entries = []
            res.data.data.map(document => {
                let entry = {
                    start: document.date,
                    end: document.date,
                    title: document.title,
                    allDay: true,
                    id: document.id
                }
                entries.push(entry)
            })
            setEvents(entries)
        }).catch((err) => {
            if (err.response.status != 403) setMessage("Nie wszystkie wymagane dane z bazy danych udało się pobrać. Spróbuj ponownie.")
        })
        axios.get(`http://${localHost}:${port}/documents/user/${id}/withoutDate`)
            .then(res => {
                let noDateDoc = []
                res.data.map(document => {
                    let doc = {
                        id: document.id,
                        title: document.title
                    }
                    noDateDoc.push(doc)
                })
                setEventsWithoutDate(noDateDoc)
            }).catch((err) => {
                if (err.response.status != 403) setMessage("Nie wszystkie wymagane dane z bazy danych udało się pobrać. Spróbuj ponownie.")
            })
    }

    const onEventClick = (entry) => {
        axios.get(`http://${localHost}:${port}/documents/${entry.event._def.publicId}`)
            .then(res => {
                let document = res.data
                setFileId(document.id)
                setTitle(document.title)
                setCost(document.cost)
                setPaid(document.paid)
                setDateToPay(document.date)
                setTypeId(document.typeId)
                setModalEventDetails(true)
            })
    }

    function addNewType(e, typeName) {
        e.preventDefault()
        const fileDTO = {
            "name": typeName
        }
        axios.post(`http://${localHost}:${port}/types`, fileDTO)
            .then((res) => {
                setTypesOfDocuments([...typesOfDocuments, res.data])
            }).catch(() => {
                setMessage("Wystąpił błąd podczas dodawania nowego typu.")
            })
    }

    async function editDocument(e, fileId, title, idOfType, dateToPay, cost, paid) {
        e.preventDefault()
        if (cost == "" || cost == null) {
            paid = null
        } else if (paid == null) {
            paid = false
        }
        const documentDTO = {
            "title": title,
            "date": new Date(dateToPay),
            "cost": cost,
            "paid": paid,
            "typeId": idOfType
        }
        axios.put(`http://${localHost}:${port}/documents/${fileId}`, documentDTO)
            .then(() => {
                handleDatesSet(dateFromTo)
            }).catch(() => {
                setMessage("Wystąpił błąd podczas zapisywania zmian na pliku: " + title)
            })
    }

    function deleteFile(e, id) {
        e.preventDefault()
        axios.delete(`http://${localHost}:${port}/documents/${id}`).then(() => {
          setMessage("Pomyślnie usunięto plik")
          handleDatesSet(dateFromTo)
        }).catch(() => {
          setMessage("Wystąpił błąd podczas usuwania pliku")
        })
        setTimeout(function () {
          setMessage(undefined)
        }, 4000);
      }

    const onEventWithoutDateClick = (e, entryId) => {
        e.preventDefault()
        axios.get(`http://${localHost}:${port}/documents/${entryId}`)
            .then(res => {
                let document = res.data
                setFileId(document.id)
                setTitle(document.title)
                setCost(document.cost)
                setPaid(document.paid)
                setDateToPay(document.date)
                setTypeId(document.typeId)
                setModalEventDetails(true)
            })
    }

    const renderEventsWithoutDate = eventsWithoutDate.map((entry) => {
        let entryId = entry.id
        let entryTitle = entry.title
        return (
            <div key={entryId}>
                <div className='fc-daygrid-event-harness' onClick={(e) => onEventWithoutDateClick(e, entryId)}>
                    <a className='fc-event fc-event-start fc-event-end fc-daygrid-event fc-daygrid-block-event fc-h-event  fc-direction-ltr'>
                        <div className='fc-event-main'>
                            <div className='fc-event-main-frame'>
                                <div className='fc-event-title-container'>
                                    <div className='fc-event-title fc-sticky'>
                                        <div className='box'>
                                            {entryTitle}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </a>
                </div>
                <Divider />
            </div>
        )
    })

    return (
        <>
            {accessMessage && (<Alert key={"danger"} variant={"danger"}>
                {accessMessage}
            </Alert>)}
            {message && (<Alert key={"warning"} variant={"warning"}>
                {message}
            </Alert>)}
            <FullCalendar
                aspectRatio={1.8}
                events={events}
                plugins={[dayGridPlugin, interactionPlugin, multiMonthPlugin]}
                initialView="dayGridMonth"
                headerToolbar={{
                    left: 'prev next today',
                    center: 'title',
                    right: 'dayGridMonth trzyMiesiace szcześćMiesiacy Rok'
                }}
                views={{
                    'trzyMiesiace': {
                        type: 'multiMonth',
                        duration: { months: 3 }
                    },
                    'szcześćMiesiacy': {
                        type: 'multiMonth',
                        duration: { months: 6 }
                    },
                    'Rok': {
                        type: 'multiMonth',
                        duration: { months: 12 }
                    },
                    dayGridMonth: {
                        dayMaxEventRows: 5 // adjust to 6 only for timeGridWeek/timeGridDay
                      }
                }}
                locales={allLocales}
                locale={'pl'}
                firstDay={1}
                dayMaxEventRows={true}
                datesSet={(date) => handleDatesSet(date)}
                navLinks='true'
                eventClick={(data) => onEventClick(data)}
            />

            <Modal
                show={modalEventDetails}
                onHide={() => {
                    setModalEventDetails(false)

                }}
                size="lg"
                aria-labelledby="contained-modal-title-vcenter"
                centered
            >
                <Modal.Header closeButton>
                    <Modal.Title id="contained-modal-title-vcenter">
                        Edycja informacji o dokumencie
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={(e) => {
                        editDocument(e, fileId, title, typeId, dateToPay, cost, paid);
                        setModalEventDetails(false);
                    }}>
                        <Form.Group className="mb-3" controlId="formTitle">
                            <Form.Label>Tytuł</Form.Label>
                            <Form.Control value={title} type="text" onChange={e => setTitle(e.target.value)} />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="formTitle">
                            <Form.Label>Typ</Form.Label>
                            <Form.Select value={typeId} onChange={e => setTypeId(e.target.value)}>
                                <option value="-1" disabled>Wybierz typ</option>
                                {typesOfDocuments.map((types) => {
                                    return <option value={types.id} key={types.id}>{types.name}</option>
                                })}
                            </Form.Select>
                            <Button variant="success" onClick={e => {
                                e.preventDefault()
                                setAddNewTypeModalShow(true)
                            }}>
                                Dodaj nowy typ dokumentu
                            </Button>
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="formDate">
                            <Form.Label>Data do zapłaty</Form.Label>
                            <Form.Control value={dateToPay != null ? new Date(dateToPay).toISOString().substring(0, 10) : new Date()} type="date" onChange={e => setDateToPay(e.target.value)} />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="formCost">
                            <Form.Label>Koszt zapłaty</Form.Label>
                            <Form.Control value={cost != null ? cost : ""} type="number" onChange={e => setCost(e.target.value)} />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="formPaid">
                            <Form.Check
                                type={'checkbox'}
                                id={`paid`}
                                label={`Opłacony`}
                                defaultChecked={paid}
                                onChange={e => setPaid(e.target.checked)}
                            />
                        </Form.Group>

                        <Button variant="primary" type="submit">
                            Zmień
                        </Button>
                        <Button variant="danger" type="button" onClick={() => setDeleteConfirmModal(true)}>
                            Usuń
                        </Button>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="danger" onClick={(e) => {
                        e.preventDefault()
                        setModalEventDetails(false)

                    }}>Zamknij</Button>
                </Modal.Footer>
            </Modal>

            <Modal
                show={addNewTypeModalShow}
                onHide={() => setAddNewTypeModalShow(false)}
                size="lg"
                aria-labelledby="contained-modal-title-vcenter"
                centered>
                <Modal.Header closeButton>
                    <Modal.Title id="contained-modal-title-vcenter">
                        Dodanie nowego typu
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={(e) => {
                        addNewType(e, newTypeName);
                        setAddNewTypeModalShow(false);
                    }}>
                        <Form.Group className="mb-3" controlId="formTitle">
                            <Form.Label>Nazwa typu</Form.Label>
                            <Form.Control value={newTypeName} type="text" onChange={e => setNewTypeName(e.target.value)} />
                        </Form.Group>
                        <Button variant="primary" type="submit">
                            Dodaj nowy typ
                        </Button>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="danger" onClick={(e) => {
                        e.preventDefault()
                        setAddNewTypeModalShow(false)
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
                        deleteFile(e, fileId);
                        setDeleteConfirmModal(false);
                        setModalEventDetails(false);
                    }}>
                        <Form.Group className="mb-3" controlId="formTitle">
                            <Form.Label>Czy napewno chcesz usunąć dany dokument z nazwą: {title}</Form.Label>
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

            <Drawer
                PaperProps={{
                    sx: { width: "90%" },
                }}
                variant="persistent"
                anchor="left"
                open={true}
                className='box'
            >
                <h2>Brak daty</h2>
                {renderEventsWithoutDate}
            </Drawer>
        </>
    )
}

export default Calendar