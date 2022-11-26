import React, { useState } from 'react';
import logo from './logo.svg';
import './App.css';
import { generateX509Cert } from "./service";
import {isBoolean} from "util";


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
    let profileRawData: string | undefined = "";
    let profileStringData: string = "";
    let profileData: ProfileType = JSON.parse( '{"picture": "", "access_token": ""}');
    try {
        profileRawData = !!profileRawDataContainer ? profileRawDataContainer.value : "";
        profileStringData = atob( profileRawData || "" );
        profileData = JSON.parse( profileStringData || '{"picture": undefined, "access_token": undefined}');
    }
    catch (e) {

    }



    // let sessionRawDataContainer: any = document.getElementById("session");
    // let sessionRawData: string | undefined = !!sessionRawDataContainer ? sessionRawDataContainer.value : "";
    let sessionRawData: string | undefined =
        window.location.href
            .split('?')
            [1]
            .split('&')
            .filter( (kv: string) => kv.split('=')[0] === 'state' )
            .reduce( (final, current) => current, '=')
            .split('=')
            [1]
    ;


    // /oauth2/preauthorize?state=eyJhY2Nlc3NfdGltZSI6MTY2OTMwNTY1ODY2OCwic2Vzc2lvbiI6eyJpZCI6NiwiY29kZSI6IjVmZWNlYjY2ZmZjODZmMzhkOTUyNzg2YzZkNjk2Yzc5YzJkYmMyMzlkZDRlOTFiNDY3MjlkNzNhMjdmYjU3ZTkiLCJvcGVuIjoiMjAyMi0xMS0yNCAxNjo1OTozOCIsImFjY2VzcyI6bnVsbCwiZW5hYmxlZCI6bnVsbCwiZXhwIjoiMjAyMy0wMi0yMiAxNjo1OTozOCIsImNsb3NlZCI6bnVsbCwiY3VzdG9tZXIiOm51bGwsInN0YXR1cyI6ImFjdGl2ZSIsImV4cGlyZWQiOnRydWUsImFjdGl2ZSI6dHJ1ZSwibGl2ZSI6ZmFsc2V9LCJwbGF0Zm9ybSI6eyJpZCI6MiwibmFtZSI6Ik1vYmlsZSBBZ2VudCBEaWFnbm9zdGljIFBvcnRhbCIsImV4cCI6bnVsbCwib3BlbiI6IjIwMjMtMTEtMjAiLCJsb2dvRmlsZVBhdGgiOm51bGwsImxvZ29GaWxlVXJsIjpudWxsLCJzZXNzaW9ucyI6bnVsbCwiZW5hYmxlZCI6bnVsbCwibG9nb0ZpbGVDb250ZW50IjpudWxsfX0%3D&code=4%2F0AfgeXvsd3_3bCMU4timLPxO6WQRwKi4nYB1uUMK4Ifi6k7MTyO2kXQW-Hin4OvvJoiw5uA&scope=profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile

    let sessionStringData: string = atob( sessionRawData.split('%3D').join('=') );
    let sessionData = JSON.parse( sessionStringData || '{}');

    const [profilePic, setProfilePic] = useState<string | undefined>();
    const [handshakeDone, setHandshakeDone] = useState<boolean>( () => false );


    console.log("profileData");
    console.log(profileData);

    console.log("sessionData");
    console.log(sessionData);


    if( !!profileData.picture && !profilePic ) {
        fetch(
            profileData.picture,
            {
                // headers: { Authorization: `Bearer ${ profileData.access_token }` }
            }
        )
          .then(response => response.blob())
          .then(imageBlob => {
              setProfilePic( URL.createObjectURL(imageBlob) );

          })
          .catch( e => {
            console.log("errore al caricamento della pic");
            console.log( e );
          })
      ;
    }


    if( !handshakeDone ) {

        setHandshakeDone( true );

        generateX509Cert( sessionData.pin )
            .then( ({ certificate: string, alg: any, publicKey: string }) => {

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
                        console.log("response");
                        console.log( response );


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

    }

    // invio chiave pubblica per questa sessione


    // TODO e2ee
    // java side
    // https://neilmadden.blog/2016/05/20/ephemeral-elliptic-curve-diffie-hellman-key-agreement-in-java/
    // https://gist.github.com/zcdziura/7652286
    // https://stackoverflow.com/questions/66060346/trying-to-create-a-ecdh-keypair-in-java
    // https://stackoverflow.com/questions/34096955/how-to-serialize-and-consume-ecdh-parameters-in-java
    // https://stackoverflow.com/questions/51861056/javascript-java-ecdh
    //


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

        <pre>
        {
            JSON.stringify( profileData, null, 2 )
        }
        </pre>

      </header>
    </div>
  );
}

export default App;
