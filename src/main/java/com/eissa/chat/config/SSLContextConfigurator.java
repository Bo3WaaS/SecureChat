package com.eissa.chat.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class SSLContextConfigurator {

	private static SSLContextConfigurator instance;

	private SSLContextConfigurator() {

	}

	public static SSLContextConfigurator getInstance() {
		if (instance == null) {
			instance = new SSLContextConfigurator();
		}
		return instance;
	}

	public SSLContext getServerSSLContext(String keyStore, char[] keyStorePass)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, KeyManagementException {

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(getStore(keyStore, keyStorePass), keyStorePass);

		SSLContext context = SSLContext.getInstance("TLSv1.2");
		context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());

		return context;
	}
	
	public SSLContext getServerSSLContext(String keyStore, char[] keyStorePass, String trustStore, char[] trustStorePass)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, KeyManagementException {

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(getStore(keyStore, keyStorePass), keyStorePass);
		
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(getStore(trustStore, trustStorePass));

		SSLContext context = SSLContext.getInstance("TLSv1.2");
		context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

		return context;
	}

	public SSLContext getClientSSLContext(String trustStore, char[] trustStorePass)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, KeyManagementException {

		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(getStore(trustStore, trustStorePass));

		SSLContext context = SSLContext.getInstance("TLSv1.2");
		context.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

		return context;
	}
	
	public SSLContext getClientSSLContext(String keyStore, char[] keyStorePass, String trustStore, char[] trustStorePass)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, KeyManagementException {

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(getStore(keyStore, keyStorePass), "client".toCharArray());
		
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(getStore(trustStore, trustStorePass));

		SSLContext context = SSLContext.getInstance("TLSv1.2");
		context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

		return context;
	}
	
	public SSLContext getClientSSLContext(String trustStore, char[] trustStorePass, String protocolVersion)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, KeyManagementException {

		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(getStore(trustStore, trustStorePass));

		SSLContext context = SSLContext.getInstance(protocolVersion);
		context.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

		return context;
	}

	private KeyStore getStore(String path, char[] pass) throws NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, KeyStoreException {
		KeyStore keyStore = KeyStore.getInstance("JKS");
		URL url = getClass().getClassLoader().getResource(path);
		InputStream inputStream = url.openStream();
		keyStore.load(inputStream, pass);
		return keyStore;
	}

}
