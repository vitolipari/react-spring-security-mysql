import React, {useState} from "react";
import {sha256} from "js-sha256";
import {ActionButton} from "../components/action-button";
import logo from '../logo.svg';
import '../App.css';
import moment from "moment";



const loadAllRoles = () => (
    fetch("/api/v1/role")
        .then( result => result.json() )
        .catch(e => Promise.reject( e ))
)

export const SignupPage = props => {

    /*
    email: <string>
    password: <string>
    roles: [ <string> ]
     */

    const [roles, setRoles] = useState();
    const [selectedRoles, setSelectedRoles] = useState();
    const [data, setData] = useState();
    // const [email, setEmail] = useState();
    // const [password, setPassword] = useState();

    if(!roles) {
        loadAllRoles()
            .then( list => {
                console.log("lista dei ruoli");
                console.log( list );
                setRoles( list );
            })
            .catch( e => {
                console.log("ERROR");
                console.error(e);
            })
    }

    return (


        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo" />
                <div className={"signin-form"}>
                    <div>
                        <label>Email</label>
                        <input
                            type={"email"}
                            name={"email"}
                            id={"email"}
                            onChange={ changeEvent => {
                                setData({
                                    ...data,
                                    email: changeEvent.target.value
                                })
                            }}
                        />
                    </div>
                    <div>
                        <label>Password</label>
                        <input
                            type={"password"}
                            name={"password"}
                            id={"password"}
                            onChange={ changeEvent => {
                                setData({
                                    ...data,
                                    password: sha256(changeEvent.target.value)
                                })
                            }}
                        />
                    </div>
                    <div>
                        <label>DoB</label>
                        <input
                            type={"dob"}
                            name={"dob"}
                            id={"dob"}
                            onChange={ changeEvent => {
                                setData({
                                    ...data,
                                    dob: moment( changeEvent.target.value, "DD/MM/YYYY" ).format()
                                })
                            }}
                        />
                    </div>
                    <div>
                        <label>Phone Number</label>
                        <input
                            type={"phone"}
                            name={"phone"}
                            id={"phone"}
                            onChange={ changeEvent => {
                                setData({
                                    ...data,
                                    phone: changeEvent.target.value
                                })
                            }}
                        />
                    </div>
                    <div>
                        <label>Roles</label>
                        <select
                            id={"roles"}
                            name={"roles"}
                            className={""}
                            onSelect={ selectedRole => {
                                console.log("role selected");
                                console.log(selectedRole);

                                setSelectedRoles([ selectedRole ]);
                            }}
                        >
                            {
                                (!!roles && (roles instanceof Array))
                                    ? (
                                        roles
                                            .map( role => (
                                                <option value={ role.id }>{ role.name }</option>
                                            ))
                                    )
                                    : null
                            }
                        </select>
                    </div>
                    <ActionButton
                        promise={ () => {


                            let payload = {
                                ...data,
                                roles: selectedRoles
                            };

                            console.log("invio i dati");
                            console.log( payload );


                            return (
                                fetch(
                                    "/api/v1/customer/signup",
                                    {
                                        method: "POST",
                                        body: JSON.stringify( payload ),
                                        headers: {
                                            "Content-Type": "application/json"
                                        }
                                    }
                                )
                                .then( response => response.json() )
                                .catch(e => Promise.reject( e ))
                            )
                        }}
                        onResult={ result => {
                            // TODO
                            console.log( result );
                        }}
                        onError={ err => {
                            console.log("ERROR");
                            console.error( err );
                        }}
                        className={"custom-button"}
                        buttonText={
                            <span>Save</span>
                        }
                        waitingMessage={"Waiting"}
                    />
                </div>
            </header>
        </div>



    );
}
