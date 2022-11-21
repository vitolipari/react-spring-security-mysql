import { extractDataFromServerSide, rawDataFromServerSide } from "../service";

export const getPlatformSessionData = () => extractDataFromServerSide();

export const getRawPlatformSessionData = () => rawDataFromServerSide();
