package com.obomprogramador.samples.microservices.grpcserverjava;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Scanner;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import com.obomprogramador.grpc.VerifySignature;

public class TestVerifier {

	@Test
	public void test() throws InvalidKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, SignatureException, IOException, DecoderException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		String keystorePath = "*";
		String alias = "meucertificado";
		String keystorePassword = "teste001";
		InputStream textFile = classloader.getResourceAsStream("arquivo.txt");
		String signature = "8ed7b4235f21db78c92e69082df3874c03d4135515cb04ff1592e66d70999d56c504dd8f6dd275f870873639ea8803ddae40272465101935a19a1877c0f07715f0cb65beb839dbf33d691acc30bd3a1af6bcc42a1b86215c6cc230e7f2ff2bcff0452df651c89659a2a6f4c8364f86ab2fccac5d7ca4d15654839aa9723e9c70f15f0699037e0745947f5253545f66b7cd3b549f9e94066c319c4e5945dddf6bafebf165c984cf60c2b4fb4ae8aade21f0a88a637161c9cb6314cf4fd42ad4c4a50337b911126f188e77dc83aeaed97338a5ee53ddc0c3575041413ab11655129f15418838a2a531516276cda5df1f814f3c3ae8986c6663533a3f31aba73e19";
		String text = new Scanner(textFile).useDelimiter("\\Z").next();;
		boolean resultado = VerifySignature.verify(signature, text, keystorePath, alias, keystorePassword);
		assertTrue(resultado);
		text += ".";
		resultado = VerifySignature.verify(signature, text, keystorePath, alias, keystorePassword);
		assertFalse(resultado);
	}

}
