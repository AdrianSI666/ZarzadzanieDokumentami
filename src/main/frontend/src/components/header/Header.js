import React from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import './Header.css'
import { Link } from 'react-router-dom';
import inMemoryJWTMenager from "../../services/inMemoryJWTMenager";

function Header(){
    const isLoggedIn = inMemoryJWTMenager.getToken()!=null;
    let button;
    if (isLoggedIn) {      
        button = <Link className="nav-link" to="/login" onClick={inMemoryJWTMenager.deleteToken()}>Logout</Link>;    
    } 
    else {      button = <Link className="nav-link" to="/login">Login</Link>;    
    }
    return(
        <nav className="navbar navbar-expand-lg navbar-light, header">
            <div className="container-fluid">
                <span className="navbar-brand">Navigation</span>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNav">
                    <ul className="navbar-nav">
                        <li className="nav-item">
                        <Link className="nav-link" to="/files">Files</Link>
                        </li>
                        {button}
                    </ul>
                </div>
            </div>
        </nav>);
}

export default Header;