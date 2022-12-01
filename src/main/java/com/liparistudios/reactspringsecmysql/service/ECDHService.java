package com.liparistudios.reactspringsecmysql.service;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import javax.crypto.KeyAgreement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

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
