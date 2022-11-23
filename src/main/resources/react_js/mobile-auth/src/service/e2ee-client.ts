import pkg from 'elliptic';
import { sha256 } from "js-sha256";
// @ts-ignore
import { errorlog, showlog, convert, bytesEncodeTypes } from '@liparistudios/js-utils';

const { eddsa, ec } = pkg;

export type GenerateKeysOptionType = {
    showTimer: boolean;
    debug: boolean;
    privateKeyFormat: string;
};

export type KeyPackType = {
    algorithm: object;
    keys: object;
    publicKeyHex: string;
    privateKeyHex: string
};

export type X509CertType = {

};

export type X509CertOptType = {};

export const curveName: string = "secp256k1";
//const curveName = "curve25519";
//const curveName = "ed25519"; va bene per eddsa
let processTime = 0;


export const generateX509Cert = (seed: string | undefined, option: X509CertOptType = {}): X509CertType => {


    /*
    https://github.com/PeculiarVentures/x509
    https://www.npmjs.com/package/@peculiar/x509
    https://kellyjonbrazil.github.io/jc/docs/parsers/x509_cert
    https://smallstep.com/blog/x509-certificate-flexibility/
    https://security.stackexchange.com/questions/44251/openssl-generate-different-types-of-self-signed-certificate
    https://msol.io/blog/tech/create-a-self-signed-ecc-certificate/
    https://connect2id.com/products/nimbus-jose-jwt/openssl-key-generation
    https://stackoverflow.com/questions/11992036/how-do-i-create-an-ecdsa-certificate-with-the-openssl-command-line
    https://stackoverflow.com/questions/71478504/how-to-generate-ca-certificate-for-certains-ciphersuites
    https://superuser.com/questions/1062275/using-ecdh-with-openssl
    https://developers.yubico.com/PIV/Guides/Generating_keys_using_OpenSSL.html
    https://www.digitalocean.com/community/tutorials/how-to-create-a-self-signed-ssl-certificate-for-nginx-on-debian-10
     */






}


export const generateKeys = (pwd: string, options: GenerateKeysOptionType | undefined, ...params: any[]): Promise<KeyPackType> => {
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
                    privateKeyHex: privateKey
                });


            })
    );
}


export const generateSessionKey = (remoteRawHexPublicKey, options, ...params) => {



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
                let sessionKey = keyPack.keys.derive(remotePublicKey.getPublic()).toString(16);

                return sessionKey;
            })



    );



}

export const createDigitalSignatureFor = (dataToSign, privateKey, /*publicKey, */algorithm, options) => {

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
            .then( pwdHex => {

                let hash = sha256( dataToSign );
                let signature = algorithm.keyFromPrivate( pwdHex, "hex" ).sign( hash );
                let hexSignature =
                    signature
                        .toDER()
                        .map((byte) => {
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
