import pkg from 'elliptic';
import { sha256 } from "js-sha256";
// @ts-ignore
import { errorlog, showlog, convert, bytesEncodeTypes } from '@liparistudios/js-utils';
import moment from 'moment';
// @ts-ignore
import jwkToPem from 'jwk-to-pem';

const { eddsa, ec } = pkg;

export type GenerateKeysOptionType = {
    showTimer: boolean;
    debug: boolean;
    privateKeyFormat: string;
};


/**
 *
 * TODO guarda questo
 * https://github.com/junkurihara/jscu/tree/develop/packages/js-x509-utils
 * meglio questo
 * https://www.npmjs.com/package/jwk-to-pem
 *
 *
 *
 * jwk
 * The "d" parameter is the private key's ArrayBuffer, url-encoded in Base64.
 * The "x" parameter is the first half of the uncompressed public key in an ArrayBuffer, url-encoded as a Base64 string.
 * The "y" parameter is the second half of the uncompressed public key in an ArrayBuffer, url-encoded as a Base64 string.
 * ( new Uint8Array([ ... ]) ).buffer > String.fromCharCode > bta() > replace(/\//g, '_') > replace(/=/g, '') > replace(/\+/g, '-')
 */
export type JwkType = {
    kty: string;
    crv: string;
    x: string;
    y: string;
    d?: string;
    kid: string;
};


export type KeyPackType = {
    algorithm: object;
    keys: object;
    publicKeyHex: string;
    privateKeyHex: string;
    jwk: JwkType;
};

export type X509CertType = {
    "Version": string;
    "Serial Number":  string;
    // "Subject": 'C=US, ST=Texas, L=Houston, O=SSL Corp/serialNumber=NV20081614243, CN=www.ssl.com'
    "Subject": {
        "Common Name": string;
    } | string;
    "Issuer": {
        "Country": string;
        "Common Name": string;
        "State": string;
        "Locality": string;
        "Organization": string;
        "Organization Unit": string;
        "Postal Code": string;
        "Street Address": string;
    },
    "Validity": {
        "Not Before": Date | string | number;
        "Not After": Date | string | number;
    },
    "Not Before": Date | string | number;
    "Not After": Date | string | number;
    "Signature Algorithm": string;
    "Public Key Info": {
        "Algorithm": string;
        "Curve": string;
        "Key Size": string | number;
        "Public Value": string | number[];
    },
    "Fingerprint": string | number[];
    "Signature": string | number[];
};

export type CreateX509CertificateOptionType = {
    timer: boolean | undefined;
    debug: boolean | undefined;
    publicKey: boolean | undefined;
    alg: boolean | undefined;
};

export type DigitalSignatureResultType = {
    hash: string;
    signature: string;
};

// export const curveName: string = "secp256k1";
export const curveName: string = "P-256";
//const curveName = "curve25519";
//const curveName = "secp256r1";  = P-256
//const curveName = "ed25519"; va bene per eddsa
let processTime = 0;




export const generateX509PemCert = (seed?: string, option?: CreateX509CertificateOptionType): Promise<{certificate: string, alg: any; publicKey: string; keys: any;}> => {
    return (
        Promise.resolve()

            .then( () => {

                return (
                    generateKeys( seed )
                        .then( (keys: KeyPackType) => {
                            return keys;
                        })
                        .catch(e => {
                            return Promise.reject( e );
                        })
                );

            })


            .then( ( pack: KeyPackType ) => {

                return ({
                    certificate: jwkToPem( pack.jwk ),
                    alg: pack.algorithm,
                    publicKey: pack.publicKeyHex,
                    keys: pack.keys
                });

            })



    );
}



export const generateX509Cert = (seed?: string, option?: CreateX509CertificateOptionType): Promise<{certificate: string, alg: any; publicKey: string; keys: any;}> => {

    let privateKey: string = '';
    let alg: any = undefined;

    return (

        Promise.resolve()

            // cert template ---------------------------------------------------------
            .then( () => {

                return({
                    "Version": '3',
                    "Serial Number": sha256( (new Date()).getTime().toString(16) ),
                    "Subject": {
                        "Common Name": 'Mobile Agent Auth Server'
                    },
                    "Issuer": {
                        "Country": 'IT',
                        "Common Name": 'CaninoSRL',
                        "State": 'Italy',
                        "Locality": 'Marsala',
                        "Organization": 'CaninoSRL',
                        "Organization Unit": 'dev',
                        "Postal Code": '91025',
                        "Street Address": 'via dello sbarco, 96'
                    },
                    "Validity": {
                        "Not Before": moment().toISOString(),
                        "Not After": moment().add(90, 'days').toISOString()
                    },
                    "Not Before": moment().toISOString(),
                    "Not After": moment().add(90, 'days').toISOString(),
                    "Signature Algorithm": 'ECDSAWithSHA256',
                    "Public Key Info": {
                        "Algorithm": 'Elliptic Curve',
                        "Curve": curveName,
                        "Key Size": '256',
                        "Public Value": ''
                    },
                    "Fingerprint": '',
                    "Signature": ''
                });

            })


            // key generation ---------------------------------------------------------
            .then( (cert: X509CertType) => {

                return (
                    generateKeys( seed )
                        .then( (keys: KeyPackType) => {
                            privateKey = keys.privateKeyHex;
                            alg = keys.algorithm;
                            cert["Public Key Info"]["Public Value"] = keys.publicKeyHex;
                            return ({certificate: cert, ...keys});
                        })
                        .catch(e => {
                            return Promise.reject( e );
                        })
                )

            })


            // signature ---------------------------------------------------------
            .then( ({certificate, algorithm, publicKeyHex, privateKeyHex, keys}) => {

                try {
                    let clonedCert = JSON.parse( JSON.stringify( certificate ) );
                    delete clonedCert.Fingerprint;
                    delete clonedCert.Signature;
                    return (
                        createDigitalSignatureFor( JSON.stringify( clonedCert ), privateKey, alg, undefined )
                            .then( (signaturePack: DigitalSignatureResultType) => {
                                certificate.Fingerprint = signaturePack.hash;
                                certificate.Signature = signaturePack.signature;

                                let fullPEM = btoa(JSON.stringify(certificate));
                                let pem = '';

                                let cursor = 0;
                                let chunckPartMagnitude = (fullPEM.length / 64);
                                chunckPartMagnitude += ((chunckPartMagnitude - Math.floor( chunckPartMagnitude )) > 0) ? 1 : 0;
                                while( cursor < chunckPartMagnitude ) {
                                    let chunck = fullPEM.substr( (cursor * 64), 64 );
                                    pem += chunck +'\n';
                                    cursor++;
                                }

                                console.log( `-----BEGIN CERTIFICATE-----\n${ pem }\n-----END CERTIFICATE-----` );

                                return ({
                                    certificate:
                                        `-----BEGIN CERTIFICATE-----${ btoa(JSON.stringify(certificate)) }-----END CERTIFICATE-----`,
                                    alg: algorithm,
                                    publicKey: publicKeyHex,
                                    keys: keys
                                });

                                // return `-----BEGIN CERTIFICATE-----
                                // ${ btoa(JSON.stringify(certificate)) }
                                // -----END CERTIFICATE-----`;
                                // return btoa(JSON.stringify(cert));

                            })
                            .catch(e => {
                                return Promise.reject( e );
                            })
                    );
                }
                catch ( e ) {
                    return Promise.reject( e );
                }
            })

    );






}


export const generateKeys = (pwd?: string, options?: GenerateKeysOptionType, ...params: any[]): Promise<KeyPackType> => {
    return (
        Promise.resolve()

			// preparing ---------------------------------------------------------------------------------------------------
			.then( () => {

				if( !!options && !!options.showTimer ) processTime = new Date().getTime();
                if( !!options && !!options.debug ) showlog("preparing");

                let privateRawStringKey = pwd || sha256( (new Date()).getTime().toString() );
				// let pwdHex = pwd;
                let pwdHex = convert( privateRawStringKey, bytesEncodeTypes.HEX );


                if( !pwd ) {
                    pwdHex = sha256( (new Date()).getTime().toString() )
                }
                else {

                    if( !!options ) {

                        switch ( options.privateKeyFormat ) {

                            case "hex":
                                pwdHex = pwd;
                                break;

                            case "ascii":
                            case "string":
                                pwdHex = convert( privateRawStringKey, bytesEncodeTypes.HEX );
                                break;

                            case "uint8array":
                                pwdHex = convert( pwd, { from: bytesEncodeTypes.UINT8ARRAY, to: bytesEncodeTypes.HEX } );
                                break;

                        }

                    }

                }


                return pwdHex;

            })

            // key generation -----------------------------------------------------------------------------------------------
            .then( pwdHex => {

                let sessionAlgorithmCurve = new ec( curveName ); // secp256k1
                let sessionAlgorithmKeys = sessionAlgorithmCurve.keyFromPrivate(pwdHex, "hex");

                //let sessionAlgorithmCurve = new eddsa( curveName ); // curve25519
                //let sessionAlgorithmKeys = sessionAlgorithmCurve.keyFromSecret(pwdHex);

                // private key
                let privateKey: string = sessionAlgorithmKeys.getPrivate().toString(16);
                //let privateKey = sessionAlgorithmKeys.getSecret();

                // public key
                let publicKey: string = sessionAlgorithmKeys.getPublic().encode("hex", false);
                //let publicKey = sessionAlgorithmKeys.getPublic();


                return ({
                    algorithm: sessionAlgorithmCurve,
                    keys: sessionAlgorithmKeys,
                    publicKeyHex: publicKey,
                    privateKeyHex: privateKey,
                    jwk: {
                        kty: "EC",
                        crv: curveName,
                        x: publicKey.substr(1, 32),
                        y: publicKey.substr(33),
                        kid: "Public Key ID"
                    }
                });


            })
    );
}



export const generateSessionKey = (remoteRawHexPublicKey: string, options: {algorithm: any, keys: any}, ...params: any[]): Promise<string> => {

    return (
        Promise.resolve()

            .then(() => {

                if( !!options && !!options.algorithm && !!options.keys ) {
                    return ({
                        algorithm: options.algorithm,
                        keys: options.keys,
                        publicKeyHex: options.keys.getPublic().encode("hex"),
                        privateKeyHex: options.keys.getPrivate().toString(16)
                    })
                }
                else {
                    return (
                        generateKeys()
                            .then( keyPack => {
                                return keyPack ;
                            })
                    );
                }


            })

            .then( keyPack => {

                // showlog("safe-package | generateSessionKey > remoteRawHexPublicKey");
                // showlog(remoteRawHexPublicKey);
                let remotePublicKey = keyPack.algorithm.keyFromPublic(remoteRawHexPublicKey, "hex");
                // showlog("safe-package | generateSessionKey > remotePublicKey");
                // showlog(remotePublicKey);
                let sessionKey: string = keyPack.keys.derive(remotePublicKey.getPublic()).toString(16);

                return sessionKey;
            })

    );

}





export const createDigitalSignatureFor = (dataToSign: string, privateKey: string | undefined | number[] | Uint8Array | Buffer, algorithm: any, options: GenerateKeysOptionType | undefined): Promise<DigitalSignatureResultType> => {

    return (
        Promise.resolve()

            // preparing ---------------------------------------------------------------------------------------------------
            .then( () => {

                if( !!options && !!options.showTimer ) processTime = new Date().getTime();
                if( !!options && !!options.debug ) showlog("preparing");

                let privateRawStringKey = privateKey || sha256( (new Date()).getTime().toString() );
                let pwdHex = privateKey;


                if( !privateKey ) {
                    pwdHex = sha256( (new Date()).getTime().toString() )
                }
                else {

                    if( !!options ) {

                        switch ( options.privateKeyFormat ) {

                            case "hex":
                                pwdHex = privateKey;
                                break;

                            case "ascii":
                            case "string":
                                pwdHex = convert( privateRawStringKey, bytesEncodeTypes.HEX );
                                break;

                            case "uint8array":
                                pwdHex = convert( privateKey, { from: bytesEncodeTypes.UINT8ARRAY, to: bytesEncodeTypes.HEX } );
                                break;

                        }

                    }

                }


                return pwdHex;

            })

            // key generation -----------------------------------------------------------------------------------------------
            .then( (pwdHex: string | Uint8Array | number[] | undefined | Buffer) => {

                let hash = sha256( dataToSign );
                let signature = algorithm.keyFromPrivate( pwdHex, "hex" ).sign( hash );
                let hexSignature =
                    signature
                        .toDER()
                        .map((byte: number) => {
                            if (byte < 0) {
                                byte = -((byte ^ 0xff) + 1);
                            }
                            //add padding at the start to ensure it's always 2 characters long otherwise '01' will be '1'
                            return byte.toString(16).padStart(2, '0');
                        })
                        .join("")
                ;

                return ({
                    hash: hash,
                    // publicKey: publicKey,
                    signature: hexSignature
                });


            })
    );
}



/*
export const verifyDigitalSignatureFor = (data, signature, publicKeyHex, algorithm, options) => {

    return (
        Promise.resolve()

            // key generation -----------------------------------------------------------------------------------------------
            .then( () => {

                let hash = sha256( data );
                let signatureVerifyStatus = algorithm.keyFromPublic(publicKeyHex, "hex").verify( hash, signature );
                if ( !signatureVerifyStatus ) return Promise.reject("invalid signature");
                return signatureVerifyStatus;

            })
    );
}
*/
