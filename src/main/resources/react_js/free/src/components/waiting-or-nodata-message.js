import React from "react";
import Spinner from "./Spinner";
import {emoj, EMOJ} from "@liparistudios/js-utils";

/**
 * Questo componente crea uno spinner di attesa o un messaggio di mandcanza dati
 * <br/>
 * props:
 * - <b>waiting</b>: <i>boolean</i> flag per lo switch da attesa a mancanza dati
 * - <b>nodataMessage</b>: <i>string</i> messaggio di mancanza dati
 * - <b>fullHeight</b>: <i>boolean</i> flag per indicare se il componente è un componente di pagina o di componente
 *
 * <br/>
 * <br/>
 * <br/>
 * <br/>
 * <table>
 *     <tr><th>prop</th><th>tipo</th><th>descrizione</th></tr>
 *     <tr><td>waiting</td><td>boolean</td><td>flag attesa o mancanza dati</td></tr>
 *     <tr><td>nodataMessage</td><td>string</td><td>testo mancanza dati</td></tr>
 *     <tr><td>fullHeight</td><td>boolean</td><td>componente di pagina o di componente</td></tr>
 * </table>
 *
 *
 *
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
export const WaitingOrNodataMessage = props => (
    (!!props.waiting)
        ? (
            <div
                className={
                    "centered-block"
                    + (!!props.fullHeight ? " almost-full-height" : "")
                    + (!!props.className ? " "+ props.className : "")
                }
            >
                <Spinner color={ !!props.spinnerColor ? props.spinnerColor : "primary" } className="spinner-border-sm" />
                {
                    !!props.waitMessage
                        ? <span>&nbsp;&nbsp;{ props.waitMessage }&nbsp;&nbsp;</span>
                        : <span>&nbsp;&nbsp;in attesa...&nbsp;&nbsp;</span>
                }
            </div>
        )
        : (
            <div
                className={
                    "centered-block"
                    + (!!props.fullHeight ? " almost-full-height" : "")
                    + (!!props.className ? " "+ props.className : "")
                }
            >
                {/*<span>{ emoj(EMOJ.sad_but_relieved_face) }&nbsp;&nbsp;&nbsp;&nbsp;{ props.nodataMessage }</span>*/}
                <span>{ props.nodataMessage }</span>
            </div>
        )
)
