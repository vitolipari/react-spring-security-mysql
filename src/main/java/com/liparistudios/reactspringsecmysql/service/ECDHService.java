package com.liparistudios.reactspringsecmysql.service;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import javax.crypto.KeyAgreement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.*;
import java.util.stream.Stream;

@Service
public class ECDHService {



	public String generateSharedKeyFromExternalCertificate(String pemCertificate ) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, OperatorCreationException, IOException, InvalidKeySpecException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());


		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate( new ByteArrayInputStream(pemCertificate.getBytes()) );

		certificate.checkValidity();

		PublicKey externalPublicKey = certificate.getPublicKey();

		System.out.println("chiave esterna publica");
		System.out.println( externalPublicKey );
		System.out.println("chiave esterna publica toString");
		System.out.println( externalPublicKey.toString() );
		System.out.println("chiave esterna publica getEncoded");
		System.out.println( externalPublicKey.getEncoded() );


		System.out.println("chiave public esterna algoritmo");
		System.out.println( externalPublicKey.getAlgorithm() );
		System.out.println("chiave public esterna formato");
		System.out.println( externalPublicKey.getFormat() );

		System.out.println("estrazione coordinate dalla chiave publica esterna");
		byte[] x = ((ECPublicKey) externalPublicKey).getW().getAffineX().toByteArray();
		byte[] y = ((ECPublicKey) externalPublicKey).getW().getAffineY().toByteArray();
		System.out.println("X");
		System.out.println(Arrays.toString(x));
		System.out.println( ((ECPublicKey) externalPublicKey).getW().getAffineX() );
		System.out.println( ((ECPublicKey) externalPublicKey).getW().getAffineX().toString(10) );
		System.out.println( ((ECPublicKey) externalPublicKey).getW().getAffineX().toString(16) );
		System.out.println("Y");
		System.out.println(Arrays.toString(y));
		System.out.println( ((ECPublicKey) externalPublicKey).getW().getAffineY() );
		System.out.println( ((ECPublicKey) externalPublicKey).getW().getAffineY().toString(10) );
		System.out.println( ((ECPublicKey) externalPublicKey).getW().getAffineY().toString(16) );



		String rawExternalPublicKey = "04" + ((ECPublicKey) externalPublicKey).getW().getAffineX().toString(16) + ((ECPublicKey) externalPublicKey).getW().getAffineY().toString(16);
		System.out.println("raw public external key");
		System.out.println(rawExternalPublicKey);

		byte[] byteExternalPublicKey = new byte[rawExternalPublicKey.length() / 2];
		for (int i = 0; i < byteExternalPublicKey.length; i++) {
			int index = i * 2;
			int val = Integer.parseInt(rawExternalPublicKey.substring(index, index + 2), 16);
			byteExternalPublicKey[i] = (byte)val;
		}

		System.out.println("bytes raw external public key");
		System.out.println( Arrays.toString( byteExternalPublicKey ) );

		System.out.println("spec ECDSA");



		byte[] ecRawExternalPublicKey =
			ByteBuffer
//				.allocate(((ECPublicKey) externalPublicKey).getW().getAffineX().toByteArray().length + ((ECPublicKey) externalPublicKey).getW().getAffineY().toByteArray().length + 1)
				.allocate(((ECPublicKey) externalPublicKey).getW().getAffineX().toByteArray().length + 1)
//				.put((byte)4)
				.put((byte)3)
				.put(((ECPublicKey) externalPublicKey).getW().getAffineX().toByteArray())
//				.put(((ECPublicKey) externalPublicKey).getW().getAffineY().toByteArray())
				.array()
		;




//		byte[] ecRawExternalPublicKey = externalPublicKey.getEncoded();
//		byte[] ecRawExternalPublicKey = byteExternalPublicKey;
		ECPublicKey ecExternalPublicKey = null;
		KeyFactory externalKeyFactor = null;

		ECNamedCurveParameterSpec ecExternalNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
		System.out.println("curva");
		ECCurve curve = ecExternalNamedCurveParameterSpec.getCurve();
		System.out.println(curve);
		System.out.println(curve.toString());
		EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, ecExternalNamedCurveParameterSpec.getSeed());
		System.out.println( ellipticCurve );
		System.out.println( ellipticCurve.toString() );
		ECPoint ecPoint = ECPointUtil.decodePoint(ellipticCurve, ecRawExternalPublicKey); // va in errore Incorrect length for uncompressed encoding
		System.out.println("Point");
		System.out.println(ecPoint);
		ECParameterSpec ecParameterSpec = EC5Util.convertSpec(ellipticCurve, ecExternalNamedCurveParameterSpec);
		ECPublicKeySpec externalPublicKeySpec = new ECPublicKeySpec(ecPoint, ecParameterSpec);

		externalKeyFactor = KeyFactory.getInstance("ECDH");
		// this is externalPubicKey
		ecExternalPublicKey = (ECPublicKey) externalKeyFactor.generatePublic(externalPublicKeySpec);

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
		keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());

		KeyPair pair = keyGen.generateKeyPair();
		ECPublicKey pub = (ECPublicKey)pair.getPublic();
		ECPrivateKey pvt = (ECPrivateKey)pair.getPrivate();

		byte[] pubEncoded = pub.getEncoded();
		byte[] pvtEncoded = pvt.getEncoded();

		KeyAgreement keyAgree = KeyAgreement.getInstance("ECDH");
		keyAgree.init(pvt);
		keyAgree.doPhase(ecExternalPublicKey, true);

		byte[] sessionKey = keyAgree.generateSecret();

		System.out.println("session key");
		System.out.println(sessionKey);

		Stream.of( sessionKey )
			.map( b -> {
				System.out.print(b);
				return b;
			})
		;



		return sessionKey.toString();


	}



	public String __TO_TEST_HYBRID_generateSharedKeyFromExternalCertificate(String pemCertificate ) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, OperatorCreationException, IOException, InvalidKeySpecException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());


		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate( new ByteArrayInputStream(pemCertificate.getBytes()) );

		certificate.checkValidity();

		PublicKey externalPublicKey = certificate.getPublicKey();

		System.out.println("chiave esterna publica");
		System.out.println( externalPublicKey );
		System.out.println("chiave esterna publica toString");
		System.out.println( externalPublicKey.toString() );
		System.out.println("chiave esterna publica getEncoded");
		System.out.println( externalPublicKey.getEncoded() );


		System.out.println("chiave public esterna algoritmo");
		System.out.println( externalPublicKey.getAlgorithm() );
		System.out.println("chiave public esterna formato");
		System.out.println( externalPublicKey.getFormat() );

		System.out.println("estrazione coordinate dalla chiave publica esterna");
		byte[] x = ((ECPublicKey) externalPublicKey).getW().getAffineX().toByteArray();
		byte[] y = ((ECPublicKey) externalPublicKey).getW().getAffineY().toByteArray();
		System.out.println("X");
		System.out.println(Arrays.toString(x));
		System.out.println( ((ECPublicKey) externalPublicKey).getW().getAffineX() );
		System.out.println( ((ECPublicKey) externalPublicKey).getW().getAffineX().toString(10) );
		System.out.println( ((ECPublicKey) externalPublicKey).getW().getAffineX().toString(16) );
		System.out.println("Y");
		System.out.println(Arrays.toString(y));
		System.out.println( ((ECPublicKey) externalPublicKey).getW().getAffineY() );
		System.out.println( ((ECPublicKey) externalPublicKey).getW().getAffineY().toString(10) );
		System.out.println( ((ECPublicKey) externalPublicKey).getW().getAffineY().toString(16) );



		String rawExternalPublicKey = "04" + ((ECPublicKey) externalPublicKey).getW().getAffineX().toString(16) + ((ECPublicKey) externalPublicKey).getW().getAffineY().toString(16);
		System.out.println("raw public external key");
		System.out.println(rawExternalPublicKey);

		byte[] byteExternalPublicKey = new byte[rawExternalPublicKey.length() / 2];
		for (int i = 0; i < byteExternalPublicKey.length; i++) {
			int index = i * 2;
			int val = Integer.parseInt(rawExternalPublicKey.substring(index, index + 2), 16);
			byteExternalPublicKey[i] = (byte)val;
		}

		System.out.println("bytes raw external public key");
		System.out.println( Arrays.toString( byteExternalPublicKey ) );

		System.out.println("spec ECDSA");




//		byte[] ecRawExternalPublicKey = externalPublicKey.getEncoded();
		byte[] ecRawExternalPublicKey = byteExternalPublicKey;
		ECPublicKey ecExternalPublicKey = null;
		KeyFactory externalKeyFactor = null;

		ECNamedCurveParameterSpec ecExternalNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
		ECCurve curve = ecExternalNamedCurveParameterSpec.getCurve();
		EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, ecExternalNamedCurveParameterSpec.getSeed());
		System.out.println("curva");
		System.out.println( ellipticCurve );
		ECPoint ecPoint = ECPointUtil.decodePoint(ellipticCurve, ecRawExternalPublicKey);
		System.out.println("Point");
		System.out.println(ecPoint);
		ECParameterSpec ecParameterSpec = EC5Util.convertSpec(ellipticCurve, ecExternalNamedCurveParameterSpec);
		ECPublicKeySpec externalPublicKeySpec = new java.security.spec.ECPublicKeySpec(ecPoint, ecParameterSpec);

		externalKeyFactor = java.security.KeyFactory.getInstance("EC");
		// this is externalPubicKey
		ecExternalPublicKey = (ECPublicKey) externalKeyFactor.generatePublic(externalPublicKeySpec);

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDH", "BC");
		keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());

		KeyPair pair = keyGen.generateKeyPair();
		ECPublicKey pub = (ECPublicKey)pair.getPublic();
		ECPrivateKey pvt = (ECPrivateKey)pair.getPrivate();

		byte[] pubEncoded = pub.getEncoded();
		byte[] pvtEncoded = pvt.getEncoded();

		KeyAgreement keyAgree = KeyAgreement.getInstance("ECDH");
		keyAgree.init(pvt);
		keyAgree.doPhase(ecExternalPublicKey, true);

		byte[] sessionKey = keyAgree.generateSecret();

		System.out.println("session key");
		System.out.println(sessionKey);

		Stream.of( sessionKey )
			.map( b -> {
				System.out.print(b);
				return b;
			})
		;



		return sessionKey.toString();


	}


	public String __old__generateSharedKeyFromExternalCertificate(String pemCertificate ) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, OperatorCreationException, IOException, InvalidKeySpecException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());


		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate( new ByteArrayInputStream(pemCertificate.getBytes()) );

		certificate.checkValidity();

		PublicKey externalPublicKey = certificate.getPublicKey();

		System.out.println("chiave esterna publica");
		System.out.println( externalPublicKey );
		System.out.println( externalPublicKey.toString() );
		System.out.println( externalPublicKey.getEncoded() );
		System.out.println( externalPublicKey.getEncoded().toString() );


		System.out.println("chiave public esterna algoritmo e formato");
		System.out.println( externalPublicKey.getAlgorithm() );
		System.out.println( externalPublicKey.getFormat() );


		System.out.println("spec ECDSA");

		KeyPairGenerator kpg = KeyPairGenerator.getInstance( externalPublicKey.getAlgorithm() ); // "BC", "SunEC", new BouncyCastleProvider()
		// prime256v1,
		// ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec("P-256");
		ECGenParameterSpec parameterSpec = new ECGenParameterSpec("secp256r1");
		kpg.initialize(parameterSpec);
		KeyPair kp = kpg.generateKeyPair();
		byte[] ourPk = kp.getPublic().getEncoded();

		System.out.println("chiave interna publica");
		System.out.println( kp.getPublic() );
		System.out.println( kp.getPublic().toString() );
		System.out.println( kp.getPublic().getEncoded() );
		System.out.println( kp.getPublic().getEncoded().toString() );

		System.out.println("chiave publica interna algoritmo e formato");
		System.out.println( kp.getPublic().getAlgorithm() );
		System.out.println( kp.getPublic().getFormat() );



		/*
		KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
		X509EncodedKeySpec pkSpec = new X509EncodedKeySpec( externalPublicKey.getEncoded() );
		PublicKey otherPublicKey = kf.generatePublic(pkSpec);
		System.out.println("chiave esterna publica ricalcolata");
		System.out.println( otherPublicKey );
		System.out.println( otherPublicKey.toString() );
		System.out.println( otherPublicKey.getEncoded() );
		System.out.println( otherPublicKey.getEncoded().toString() );
		 */


		System.out.println("controllo chiave di sessione");


		KeyAgreement ka = KeyAgreement.getInstance("ECDH");
		ka.init(kp.getPrivate());
		ka.doPhase(externalPublicKey, true);
		// ka.doPhase(otherPublicKey, true);


		System.out.println("generating shared");

		// Read shared secret
		byte[] sharedSecret = ka.generateSecret();
		StringBuilder sb = new StringBuilder();
		for(byte b: sharedSecret) {
			sb.append(String.format("%02X", b));
		}
		System.out.println("Chiave di sessione in hex");
		System.out.println(sb.toString());

		// Derive a key from the shared secret and both public keys
		MessageDigest hash = MessageDigest.getInstance("SHA-256");
		hash.update(sharedSecret);


		byte[] sharedHashedKey = hash.digest();
		sb = new StringBuilder();
		for(byte b: sharedHashedKey) {
			sb.append(String.format("%02X", b));
		}
		System.out.println("Chiave di sessione hashata in hex");
		System.out.println(sb.toString());


		System.out.println("chiave di sessione in b64");
		System.out.println(Base64.getEncoder().encodeToString(sharedSecret));

		System.out.println("chiave hashata di sessione in b64");
		System.out.println(Base64.getEncoder().encodeToString(sharedHashedKey));



		byte[] encoded = kp.getPublic().getEncoded();
		SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance( ASN1Sequence.getInstance(encoded) );

		Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);

		X509v3CertificateBuilder ownCertificate =
			new X509v3CertificateBuilder(
				new X500Name( certificate.getIssuerDN().toString() ),
				BigInteger.ONE,
				startDate, endDate,
				new X500Name( certificate.getSubjectDN().toString() ),
				subjectPublicKeyInfo
			)
		;

		X509CertificateHolder certHolder = ownCertificate.build( new JcaContentSignerBuilder("SHA256withECDSA").setProvider("BC").build( kp.getPrivate() ) );

		System.out.println("creato certificato");
		System.out.println( certHolder.getEncoded() );
		System.out.println( certHolder.getEncoded().toString() );
		System.out.println( certHolder.toString() );
		System.out.println( certHolder.toASN1Structure().toString() );
		System.out.println( certHolder.toASN1Structure().getEncoded() );

		sb = new StringBuilder();
		for(byte b: certHolder.getEncoded()) {
			sb.append(String.format("%02X", b));
		}

		return sb.toString();

	}

}
