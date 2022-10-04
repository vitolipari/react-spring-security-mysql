import React from "react";
import "./index.css";

export const DotLoaderComponent = props => (
	<div
		className={"loader" + (!!props.className ? props.className : "")}
		style={
			!!props.color
				? { background: props.color }
				: {}
		}
	/>
);
