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
import { Provider } from 'react-redux';
import store from './components/redux/store';
import { PersistGate } from 'redux-persist/integration/react';
import { persistStore } from 'redux-persist';
import Calendar from './components/calendar/FullCalendar';
import ManageUsers from './components/manage/ManageUsers';

let persistor = persistStore(store)
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(

    <Provider store={store}>
      <PersistGate persistor={persistor}>
      <Router>
        <Routes>
          <Route path={"/"} element={<Root />}>
            <Route path={"login"} element={<Login />} />
            <Route path={"files"} element={<Files />} />
            <Route path={"user/files"} element={<UserFiles />} />
            <Route path={"calendar"} element={<Calendar />} />
            <Route path={"manage/users"} element={<ManageUsers />}/>
          </Route>
        </Routes>
      </Router>
      </PersistGate>
    </Provider>
);
