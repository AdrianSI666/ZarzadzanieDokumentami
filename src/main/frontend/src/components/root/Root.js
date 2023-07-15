import React, {useState} from 'react';
import './Root.css';
import Header from '../header/Header';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Outlet } from 'react-router-dom';

function Root() {
  const [address] = useState({
    localHost: "localhost",
    port: "8091",
  });
  return (
    <div className="container">
      <div className="row">
        <div className="col-xs-10 col-xs-offset-1">
          <Header />
        </div>
      </div>
      <div className='row'>
        <div className="Root">
          <Outlet context={[address]}/>
        </div>
      </div>
    </div>
  );
}

export default Root;
