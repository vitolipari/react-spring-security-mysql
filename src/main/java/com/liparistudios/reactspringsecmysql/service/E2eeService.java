package com.liparistudios.reactspringsecmysql.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liparistudios.reactspringsecmysql.model.ECDHKeyPack;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.springframework.stereotype.Service;

import javax.crypto.KeyAgreement;
import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

@Service
public class E2eeService {

	private byte[] hexToBytes(String string) {
		int length = string.length();
		byte[] data = new byte[length / 2];
		for (int i = 0; i < length; i += 2) {
			data[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4) + Character
				.digit(string.charAt(i + 1), 16));
		}
		return data;
	}


	public void xxx( byte[] otherPk )
		throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
		kpg.initialize(256);
		KeyPair kp = kpg.generateKeyPair();
		byte[] ourPk = kp.getPublic().getEncoded();

		// Display our public key
		System.out.println("Public Key");
		System.out.println( printHexBinary(ourPk) );

		KeyFactory kf = KeyFactory.getInstance("EC");
		X509EncodedKeySpec pkSpec = new X509EncodedKeySpec(otherPk);
		PublicKey otherPublicKey = kf.generatePublic(pkSpec);

		// Perform key agreement
		KeyAgreement ka = KeyAgreement.getInstance("ECDH");
		ka.init(kp.getPrivate());
		ka.doPhase(otherPublicKey, true);

		// Read shared secret
		byte[] sharedSecret = ka.generateSecret();
		System.out.println("Shared secret");
		System.out.println( printHexBinary(sharedSecret) );

		// Derive a key from the shared secret and both public keys
		MessageDigest hash = MessageDigest.getInstance("SHA-256");
		hash.update(sharedSecret);
		// Simple deterministic ordering
		List<ByteBuffer> keys = Arrays.asList(ByteBuffer.wrap(ourPk), ByteBuffer.wrap(otherPk));
		Collections.sort(keys);
		hash.update(keys.get(0));
		hash.update(keys.get(1));

		byte[] derivedKey = hash.digest();
		System.out.println("Final key");
		System.out.println( printHexBinary(derivedKey) );

	}





	public void extractPublicKeyFromCertificate() {}



	public ECPublicKey hexPublicKeyToPublicKey(String rawPubKey, String curveName) throws NoSuchAlgorithmException {
		byte[] rawPublicKey = DatatypeConverter.parseHexBinary(rawPubKey);
		ECPublicKey ecPublicKey = null;
		KeyFactory kf = null;

		ECNamedCurveParameterSpec ecNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec(curveName);
		ECCurve curve = ecNamedCurveParameterSpec.getCurve();
		EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, ecNamedCurveParameterSpec.getSeed());
		ECPoint ecPoint = ECPointUtil.decodePoint(ellipticCurve, rawPublicKey);
		ECParameterSpec ecParameterSpec = EC5Util.convertSpec(ellipticCurve, ecNamedCurveParameterSpec);
		ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(ecPoint, ecParameterSpec);

		kf = java.security.KeyFactory.getInstance("EC");

		try {
			ecPublicKey = (ECPublicKey) kf.generatePublic(publicKeySpec);
		} catch (Exception e) {
			System.out.println("Caught Exception public key: " + e.toString());
		}

		return ecPublicKey;
	}


	public ECDHKeyPack ecdhKeysFromExternalPublicKey( String otherPublicKeyHexFormat ) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException {

		String ecdhCurvenameString = "secp256r1";	// was secp256r1
		// standard curvennames
		// secp256r1 [NIST P-256, X9.62 prime256v1]
		// secp384r1 [NIST P-384]
		// secp521r1 [NIST P-521]

		ECPublicKey externalPk = hexPublicKeyToPublicKey( otherPublicKeyHexFormat, ecdhCurvenameString );
		//byte[] otherPk = hexToBytes( externalPk );

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "SunEC");
		ECGenParameterSpec ecParameterSpec = new ECGenParameterSpec(ecdhCurvenameString);
		keyPairGenerator.initialize(ecParameterSpec);
		KeyPair ecdhKeyPair = keyPairGenerator.genKeyPair();
		PrivateKey privateKey = ecdhKeyPair.getPrivate();
		PublicKey publicKey = ecdhKeyPair.getPublic();
		System.out.println("privateKey: " + privateKey);
		System.out.println("publicKey: " + publicKey);


		/*
		// Perform key agreement
		KeyFactory kf = KeyFactory.getInstance("EC");
		X509EncodedKeySpec pkSpec = new X509EncodedKeySpec( externalPk ); // errore, i byte[]  della public key devono provenire da una public key in formato PEM
		PublicKey otherPublicKey = kf.generatePublic(pkSpec);
		 */

		KeyAgreement ka = KeyAgreement.getInstance("ECDH");
		ka.init(privateKey);
		// ka.doPhase(otherPublicKey, true);
		ka.doPhase(externalPk, true);

		// Read shared secret
		byte[] sharedSecret = ka.generateSecret();
		System.out.println("Shared secret");
		System.out.println( printHexBinary(sharedSecret) );

		return null;

	}

	public ECDHKeyPack checkCertificate( String pemCert ) throws JsonProcessingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

		String certPEM =
			pemCert
				.lines()
				.collect(Collectors.joining())
				.replace("-----BEGIN CERTIFICATE-----", "")
				.replace("-----END CERTIFICATE-----", "")
				.replace("\"", "")
		;

		System.out.println("cert content");
		System.out.println( certPEM );

		byte[] bytes = null;
		String certJson = new String();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> certMap = null;

		System.out.println("estrazione b64");

		// decode base64
//		bytes = Base64.getDecoder().decode( certPEM );
		bytes = Base64.getUrlDecoder().decode( certPEM );
		certJson = new String(bytes);

		// json to Map
		certMap = mapper.readValue(certJson, Map.class);

		System.out.println( certJson );
		System.out.println( certMap );


		// extract values

		// check validity
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSX");

		System.out.println("not before");
		System.out.println( ( (Map<String, Object>) certMap.get("Validity")).get("Not Before") );
		LocalDateTime startValidity = LocalDateTime.parse( ( (Map<String, Object>) certMap.get("Validity")).get("Not Before").toString(), formatter );
		System.out.println("now");
		LocalDateTime now = LocalDateTime.now();
		System.out.println( now );
		System.out.println("not after");
		System.out.println( ( (Map<String, Object>) certMap.get("Validity")).get("Not After") );
		LocalDateTime endValidity = LocalDateTime.parse( ( (Map<String, Object>) certMap.get("Validity")).get("Not After").toString(), formatter );


		if( now.isBefore( endValidity ) && now.isAfter( startValidity ) ) {

			// check fingerprint



			// check signature
			System.out.println("fingerprint");
			System.out.println( certMap.get("Fingerprint") );
			System.out.println("signature");
			System.out.println( certMap.get("Signature") );
			System.out.println("public key");
			System.out.println( ((Map<String, Object>)certMap.get("Public Key Info")).get("Public Value") );



			return ecdhKeysFromExternalPublicKey( ((Map<String, Object>)certMap.get("Public Key Info")).get("Public Value").toString() );


		}
		else return null;
	}

/*
	public String ecdhSharedKeyFromExternalCertificate( String pemCert ) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, JsonProcessingException {
		String certPublicKey = checkCertificate( pemCert );
		if( certPublicKey != null ) {
			String sharedHexKey = ecdhSharedKeyFromExternalPublicKey( certPublicKey );
		}
		else {

		}
		return null;
	}
 */

	public ECDHKeyPack ecdhKeysFromExternalCertificate( String pemCert ) throws JsonProcessingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
		ECDHKeyPack keys = checkCertificate( pemCert );
		if( keys != null ) {
			return keys;
		}
		else {
			return null;
		}
	}



}
