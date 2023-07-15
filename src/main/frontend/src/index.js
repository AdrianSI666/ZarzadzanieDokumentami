import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import {
  BrowserRouter as Router,
  Routes,
  Route
} from "react-router-dom";
import Root from './components/root/Root.js';
import Login from './components/login/Login'
import Files from './components/files/Files';
import UserFiles from './components/UserFiles/UserFiles';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <Router>
      <Routes>
        <Route path={"/"} element={<Root />}>
          <Route path={"login"} element={<Login />} />
          <Route path={"files"} element={<Files />} />
          <Route path={"user/files"} element={<UserFiles />} />
        </Route>
      </Routes>
    </Router>
  </React.StrictMode>
);
