import React from "react";
import "./index.css";
import {httpTimer, showlog} from "@liparistudios/js-utils";
import {DotLoaderComponent} from "../dot-loader-component";


/**
 *
 */
export class ActionButton extends React.Component {
	
	state = {
		waiting: false
	};
	
	/**
	 *
	 * @param props
	 */
	constructor( props ) {
		super( props );
	}
	
	
	/**
	 *
	 * @returns {JSX.Element|null}
	 */
	render() {
		
		return (
			(this.props.visible === true || this.props.visible === undefined )
				? (
					<div
						className={
							"action-button "+
							(
								!!this.props.className
									? this.props.className
									: ""
							) +
							(
								!!this.props.disabled
									? " disabled-action-button"
									: ""
							)
						}
						onClick={ clickEvent => {
							
							clickEvent.stopPropagation();
							clickEvent.preventDefault();
							
							if( !!!this.state.waiting && !this.props.disabled) {
								if( typeof this.props.onProcess === "function" ) this.props.onProcess();
								this.setState({
										...this.state,
										waiting: true
									},
									() => {
										Promise.race([
												httpTimer.start(),
												this.props.promise( clickEvent )
													.then( result => result )
													.catch(e => Promise.reject(e))
											])
											.then(result => {
												showlog("race finito");
												showlog(result);
												this.setState({
														...this.state,
														waiting: false
													},
													() => {
														if( typeof this.props.onResult === "function" ) this.props.onResult( result );
													})
											})
											.catch(e => {
												console.error("error");
												showlog(e);
												this.setState({
														...this.state,
														waiting: false
													},
													() => {
														if( typeof this.props.onError === "function" ) this.props.onError( e );
													})
											})
										;
									}
								)
							}
							
						}}
					>
						{
							(!!this.state.waiting)
								? (
									<div className={"action-button-in-wait"}>
										<DotLoaderComponent color={"#fffa"} className="" />
										<span>{
											(!!this.props.waitingMessage)
												? String( this.props.waitingMessage )
												: "  in attesa...  "
										}</span>
									</div>
								)
								: (
									(!!this.props.buttonText)
										? this.props.buttonText
										: "Salva"
								)
						}
					</div>
				)
				: null
		);
		
	}
	
}
