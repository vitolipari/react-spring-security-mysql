import { PlatformType, SessionType } from "../model";

const extractDataFromServerSide = (): { platform: PlatformType | undefined, session: SessionType | undefined } => {
    let data = { platform: undefined, session: undefined };
    let rawDataContainer: HTMLInputElement | null = document.getElementById("data") as HTMLInputElement;
    if( !!rawDataContainer ) {
        let rawData = rawDataContainer.value;
        data = JSON.parse( atob( rawData ) );
    }    
    return data;
}

const rawDataFromServerSide = (): string => {
    let rawData: string = '';
    let rawDataContainer: HTMLInputElement | null = document.getElementById("data") as HTMLInputElement;
    if( !!rawDataContainer ) {
        rawData = rawDataContainer.value;
    }    
    return rawData;
}

export { extractDataFromServerSide, rawDataFromServerSide };