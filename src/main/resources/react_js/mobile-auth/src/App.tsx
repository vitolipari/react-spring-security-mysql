import React, { useState } from 'react';
import logo from './logo.svg';
import './App.css';
import {generateKeys, generateX509Cert, KeyPackType, X509CertType} from "./service";


type ProfileType = {
    family_name: string;
    given_name: string;
    id: string;
    locale: string;
    name: string;
    picture: string;
    access_token: string;
    id_token: string;
};

const App = (): JSX.Element => {

    let profileRawDataContainer: any = document.getElementById("profile");
    let profileRawData: string | undefined = !!profileRawDataContainer ? profileRawDataContainer.value : "";
    let profileStringData: string = atob( profileRawData || "" );
    let profileData: ProfileType = JSON.parse( profileStringData || '{"picture": undefined, "access_token": undefined}');

    let sessionRawDataContainer: any = document.getElementById("session");
    let sessionRawData: string | undefined = !!sessionRawDataContainer ? sessionRawDataContainer.value : "";
    let sessionStringData: string = atob( sessionRawData || "" );
    let sessionData = JSON.parse( sessionStringData || '{}');

    const [profilePic, setProfilePic] = useState<string | undefined>();


    console.log("profileData");
    console.log(profileData);

    console.log("sessionData");
    console.log(sessionData);


    if( !!profileData.picture ) {
        fetch(
            profileData.picture,
            {
                // headers: { Authorization: `Bearer ${ profileData.access_token }` }
            }
        )
          .then(response => response.blob())
          .then(imageBlob => {
              setProfilePic( URL.createObjectURL(imageBlob) );



              generateX509Cert( sessionData.pin )
                  .then( (certificate: string ) => {

                      console.log("certificate");
                      console.log( certificate );

                      // handShake
                      fetch(
                          'http://localhost:9009/api/v1/session/handshake',
                          {
                              method: 'POST',
                              // headers: { Authorization: `Bearer ${ profileData.access_token }` },
                              body: JSON.stringify( certificate )
                          }
                      )
                          .then(response => response.json())
                          .then(response => {

                              // END

                          })
                          .catch(e => {
                              console.log("errore all'handshaking'")
                              console.log( e );
                          })

                  })
                  .catch(e => {
                      console.log("errore alla generazione delle chiavi e del certificato")
                      console.log( e );
                  })
              ;

              // invio chiave pubblica per questa sessione


              // TODO e2ee
              // java side
              // https://neilmadden.blog/2016/05/20/ephemeral-elliptic-curve-diffie-hellman-key-agreement-in-java/
              // https://gist.github.com/zcdziura/7652286
              // https://stackoverflow.com/questions/66060346/trying-to-create-a-ecdh-keypair-in-java
              // https://stackoverflow.com/questions/34096955/how-to-serialize-and-consume-ecdh-parameters-in-java
              // https://stackoverflow.com/questions/51861056/javascript-java-ecdh
              //


          })
          .catch( e => {
            console.log("errore al caricamento della pic");
            console.log( e );
          })
      ;
    }

  return (
    <div className="App">
      <header className="App-header">
        <img
            src={ profilePic || logo }
            className={ !!profilePic ? "" : "App-logo"}
            alt={!!profilePic ? "" : "logo"}
        />

        <p>
          <code>Profile</code>
        </p>
        <p>
            <pre>
            {
                JSON.stringify( profileData, null, 2 )
            }
            </pre>
        </p>

      </header>
    </div>
  );
}

export default App;
