import React, { useState } from 'react';
import logo from './logo.svg';
import './App.css';
import { PlatformType, SessionType } from './model';
import { 
  getPlatformSessionData, 
  getRawPlatformSessionData 
} from './business-logic';

type AppProps = {};


const App = ( props: AppProps ): JSX.Element => {


  const [sessionData, setSessionData] = 
    useState<{ 
      platform: PlatformType | undefined, 
      session: SessionType | undefined 
    }>({ 
      platform: undefined, 
      session: undefined 
    })
  ;
  const [pin, setPin] = useState<string>('');

  if( !sessionData.platform ) {
    console.log('rawData');
    console.log( getRawPlatformSessionData() );
    setSessionData( getPlatformSessionData() );
  }
  else {
    console.log( sessionData );
  }

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Preparing for auth
        </p>
        <div>
          <code>sessionID</code>
          <pre>{ sessionData.session?.id }</pre>
        </div>

        <div>
          <code>sessionCODE</code>
          <pre>{ sessionData.session?.code }</pre>
        </div>

        <div>
          <code>platfornID</code>
          <pre>{ sessionData.platform?.id }</pre>
        </div>

        <div>
          <code>platfornNAME</code>
          <pre>{ sessionData.platform?.name }</pre>
        </div>

        <div>
          <code>platfornLOGO_URL</code>
          <pre>{ sessionData.platform?.logoUrl }</pre>
        </div>

      </header>
    </div>
  );
}

export default App;
