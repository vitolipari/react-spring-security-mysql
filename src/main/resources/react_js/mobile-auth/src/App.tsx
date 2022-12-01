import React, { useState, useEffect } from 'react';
import logo from './logo.svg';
import './App.css';
import {generateSessionKey, generateSessionKeyByCert, generateX509Cert, generateX509PemCert} from "./service";
import {isBoolean} from "util";
import x509 from "js-x509-utils";
// import {showlog} from "@liparistudios/js-utils";


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



    useEffect((): any => {
        console.log("ComponentDidMount equivalent");


        // http://localhost:3001/s?s=5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9&p=0123456789012345&pl=2
        generateX509PemCert( sessionData.pin )
            .then( (certificate) => {

                console.log("certificate");
                console.log( certificate );




                generateSessionKeyByCert(certificate, certificate)
                    .then(r => {
                        console.log("result si generateSessionKeyByCert");
                        console.log(r);
                    })
                    .catch(e => {
                        console.log("errore generateSessionKeyByCert");
                        console.log(e);
                    })
                ;




                //
                // console.log("keys");
                // console.log( keys );
                //
                // console.log("alg");
                // console.log( alg );
                //
                // console.log("publicKey");
                // console.log( publicKey );

                // handShake
                fetch(
                    'http://localhost:9009/api/v1/session/handshake',
                    {
                        method: 'POST',
                        // headers: { Authorization: `Bearer ${ profileData.access_token }` },
                        body: certificate
                    }
                )
                    .then(response => response.json())
                    .then(response => {

                        // END
                        console.log("response");
                        console.log( response );

                        /*
                                                generateSessionKeyByCert(certificate, certificate)
                                                    .then(r => {
                                                        console.log("r");
                                                        console.log(r);
                                                    })
                                                    .catch(e => {
                                                        console.log("errore");
                                                        console.log(e);
                                                    })
                                                ;
                        */



                        /*
                        generateSessionKey( response.publicKey, { algorithm: alg, keys: keys })
                            .then( (sessionKey: string) => {


                                console.log("session key");
                                console.log(sessionKey);


                            })
                            .catch( e => {

                                console.log("Error");
                                console.log( e );

                            })
                        */


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

        return () => {
            // showlog("ComponentDidMount equivalent RETURN");
            return null;
        };
    });


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

/*
    if( !handshakeDone ) {

        setHandshakeDone( true );



    }

 */


  return (
    <div className="App">
      <header className="App-header">
        <img
            src={ profilePic || logo }
            className={ !!profilePic ? " profile-pic" : "App-logo"}
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
