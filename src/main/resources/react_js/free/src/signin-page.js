import React from "react";
import logo from "./logo.svg";
import {sha256} from "js-sha256";
import {ActionButton} from "./components/action-button";
import {useState} from "@types/react";

export const SigninPage = props => {

	const [email, setEmail] = useState();
	const [password, setPassword] = useState();

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
								setEmail( changeEvent.target.value );
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
								setPassword( sha256(changeEvent.target.value) );
							}}
						/>
					</div>
					<ActionButton
						promise={ () => {

							console.log("invio i dati");
							console.log({
								email: email,
								password: password
							});


							
							return (
								fetch(
									"/api/v1/sign-in",
									{
										method: "POST",
										body: JSON.stringify({
											email: email,
											password: password
										})
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
							<span>SignIn</span>
						}
						waitingMessage={"Waiting"}
					/>
				</div>
			</header>
		</div>
	);
}
