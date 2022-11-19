import React from 'react';
import logo from './logo.svg';
import './App.css';

type AppProps = {};
type PlatformType = {
  id: number;
  name: string;
  logoUrl: string;
};

const App = ( propr: AppProps ): JSX.Element => {

  let session: string = '';
  let pin: string = '';
  let platform: PlatformType = {id: 0, name: '', logoUrl: ''};

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Preparing for auth
        </p>      
      </header>
    </div>
  );
}

export default App;
