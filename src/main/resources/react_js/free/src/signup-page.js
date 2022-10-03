import React, {useState} from "react";
import {ActionButton} from "./action-button";

const loadAllRoles = () => (
    fetch("/api/v1/permissions")
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
    
    if(!roles) {
        loadAllRoles()
            .then( list => {
                setRoles( list );
            })
            .catch( e => {
                console.log("ERROR");
                console.error(e);
            })
    }

    return (
        <div className={""}>
            <div>
                <label>Email</label>
                <input
                    type={"email"}
                    name={"email"}
                    id={"email"}
                />
            </div>
            <div>
                <label>Email</label>
                <input
                    type={"email"}
                    name={"email"}
                    id={"email"}
                />
            </div>
            <div>
                <label>Roles</label>
                <select
                    id={}
                    name={}
                    className={}
                >
                    {
                        !!roles
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
                promise={}
                onProgress={}
                onResult={}
                onError={}
                className={}
                buttonText={}
                waitingMessage={}
            />
        </div>
    );
}
