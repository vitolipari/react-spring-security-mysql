import React, {useEffect, useState} from 'react';
import logo from './logo.svg';
import './App.css';
import {PlatformType, SessionType} from './model';
import {
    getPlatformSessionData,
    getRawPlatformSessionData
} from './business-logic';

type AppProps = {};


const App = (props: AppProps): JSX.Element => {


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

    useEffect(() => {

        setTimeout(
            function () {

                let redirectBaseDomain = `http://localhost:9009`;
                let url = [
                        process.env.REACT_APP_GOOGLE_AUTH_URL +
                        "scope=" + process.env.REACT_APP_GOOGLE_AUTH_SCOPE,
                        "redirect_uri=" + redirectBaseDomain + process.env.REACT_APP_GOOGLE_AUTH_REDIRECT_URI,
                        "client_id=" + process.env.REACT_APP_GOOGLE_CLIENT_ID,
                        "response_type=" + process.env.REACT_APP_GOOGLE_AUTH_RESPONSE_TYPE,
                        "state=" + btoa(JSON.stringify({
                            access_time: ((new Date()).getTime()),
                            ...sessionData,
                            pin: document.getElementById('pin')?.value
                        })),
                    ]
                    .join("&")
                ;

                window.location.href = url;

            },
            5000
        )

        return () => {
            // equivalent to ComponentWillUnmount
        };
    }, [sessionData]);

    if (!sessionData.platform) {
        console.log('rawData');
        console.log(getRawPlatformSessionData());
        setSessionData(getPlatformSessionData());
    } else {
        console.log("sessionData");
        console.log(sessionData);

        console.log("device");

    }

    return (
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo"/>

                {
                    (!sessionData.platform)
                        ? (
                            <p>
                                Preparing for auth
                            </p>
                        )
                        : (
                            <React.Fragment>
                                <div>
                                    <code>sessionID</code>
                                    <pre>{sessionData.session?.id}</pre>
                                </div>

                                <div>
                                    <code>sessionCODE</code>
                                    <pre>{sessionData.session?.code}</pre>
                                </div>

                                <div>
                                    <code>platfornID</code>
                                    <pre>{sessionData.platform?.id}</pre>
                                </div>

                                <div>
                                    <code>platfornNAME</code>
                                    <pre>{sessionData.platform?.name}</pre>
                                </div>

                                <div>
                                    <code>platfornLOGO_URL</code>
                                    <pre>{sessionData.platform?.logoUrl}</pre>
                                </div>
                            </React.Fragment>
                        )
                }


            </header>
        </div>
    );
}

export default App;
