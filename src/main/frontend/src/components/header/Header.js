import React from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import './Header.css'
import { Link } from 'react-router-dom';
import inMemoryJWTMenager from "../../services/inMemoryJWTMenager";
import { useDispatch, useSelector } from "react-redux";
import { loggOfUser, loggInMannager } from "../redux/config";
import { Button } from "react-bootstrap";
function Header() {
    const config = useSelector((state) => state.config)
    const dispatch = useDispatch();
    return (
        <nav className="navbar navbar-expand-lg navbar-light, header">
            <div className="container-fluid">
                <span className="navbar-brand">Nawigacja</span>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNav">
                    <div className="navbar-nav" style={{
                        width: '50%',
                        display: 'flex',
                        justifyContent: 'left'
                    }}>
                        {config.loggedIn !== false ? <>
                            <li className="nav-item">
                                <Link className="nav-link" to="/files">Wszystkie pliki</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link" to="/user/files">Twoje pliki</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link" to="/calendar">Kalendarz</Link>
                            </li>

                        </> : <></>}
                    </div>
                    <div className="ml-auto" style={{
                        width: '50%',
                        display: 'flex',
                        justifyContent: 'right'
                    }}>
                        {config.loggedIn === false ? <div>
                            <Button variant="outline-secondary">
                                <Link className="nav-link" to="/login">Login</Link>
                            </Button>

                        </div>
                            : <>
                                {config.isMannager === true ? <div className="nav-link" style={{
                                    marginTop: 10,
                                    marginRight: 10
                                }}>
                                    <Link className="nav-link" to="/manage/users">
                                    Zarządzaj kontami
                                    </Link> </div> : <></>}
                                <div>
                                <Button variant="outline-secondary">
                                    <Link className="nav-link" to="/login" onClick={() => {
                                        inMemoryJWTMenager.deleteToken()
                                        dispatch(loggOfUser())
                                        dispatch(loggInMannager(false))
                                    }}>Logout</Link>
                                </Button>
                                </div>
                            </>}
                    </div>
                </div>
            </div>
        </nav>);
}

export default Header;