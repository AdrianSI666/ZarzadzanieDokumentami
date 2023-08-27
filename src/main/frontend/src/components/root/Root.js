import React from 'react';
import './Root.css';
import Header from '../header/Header';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useOutlet } from 'react-router-dom';

function Root() {
  const outlet = useOutlet()
  return (
    <div className="container">
      
        <div className="row">
          <div className="col-xs-10 col-xs-offset-1">
            <Header />
          </div>
        </div>
        <div className='row'>
          <div className="Root">
            {outlet || <div>
              <h2>Welcome, this page can show some general information.</h2>
              <p>To do anything on this application go to login in the top right corner.</p>
              </div>
            }
          </div>
        </div>
    </div>
  );
}

export default Root;
