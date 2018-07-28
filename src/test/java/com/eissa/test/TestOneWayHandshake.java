package com.eissa.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.junit.Before;
import org.junit.Test;

import com.eissa.chat.config.SSLContextConfigurator;

public class TestOneWayHandshake {
	
	private static final String KEY_STORE = "client_truststore.jks";
	private static final char[] pass = "secret".toCharArray();
	private static SSLContextConfigurator config = SSLContextConfigurator.getInstance(); 
	
	@Before
	public void setup(){
		
	}
	
	@Test(expected=Exception.class)
	public void NoTLS_Exception() throws UnknownHostException, IOException{
		Socket socket = new Socket("127.0.0.1", 5555);
		sendDataToServer(socket);
	}
	
	@Test
	public void TLS1WayHandshake_Success() throws UnrecoverableKeyException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		SSLContext context = config.getClientSSLContext(KEY_STORE, pass);
		SSLSocketFactory socketFactory = context.getSocketFactory();
		SSLSocket socket = (SSLSocket) socketFactory.createSocket("127.0.0.1", 5555);
		
		sendDataToServer(socket);
		
	}
	
	@Test
	public void TLS1WayHandshakeWithHostnameVerification_Success() throws UnrecoverableKeyException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		SSLContext context = config.getClientSSLContext(KEY_STORE, pass);
		SSLSocketFactory socketFactory = context.getSocketFactory();
		SSLSocket socket = (SSLSocket) socketFactory.createSocket("localhost", 5555); 	// If you use 127.0.0.1 it will fail
		
		SSLParameters parameters = new SSLParameters();
		parameters.setEndpointIdentificationAlgorithm("HTTPS");				// This is used to verify the host name like how HTTPS works
		socket.setSSLParameters(parameters);
		
		sendDataToServer(socket);
		
	}
	
	@Test(expected=SSLException.class)
	public void TLS1WayHandshakeWithHostnameVerification_Exception() throws UnrecoverableKeyException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		SSLContext context = config.getClientSSLContext(KEY_STORE, pass);
		SSLSocketFactory socketFactory = context.getSocketFactory();
		SSLSocket socket = (SSLSocket) socketFactory.createSocket("127.0.0.1", 5555); 	// If you use localhost it will success
		
		SSLParameters parameters = new SSLParameters();
		parameters.setEndpointIdentificationAlgorithm("HTTPS");				// This is used to verify the host name like how HTTPS works
		socket.setSSLParameters(parameters);
		
		sendDataToServer(socket);
		
	}
	
	@Test(expected=SSLException.class)
	public void TLS1WayHandshakeNoClientSSLContext_Exception() throws UnknownHostException, IOException{
		SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket("127.0.0.1", 5555);
		sendDataToServer(socket);
	}
	
	@Test(expected=SSLException.class)
	public void TLS1WayHandshakeWithClientSSLContextNoTrustManager_Exception() throws UnknownHostException, IOException, NoSuchAlgorithmException, KeyManagementException{
		SSLContext context = SSLContext.getInstance("TLSv1.2");
		context.init(null, null, null);
		SSLSocketFactory socketFactory = context.getSocketFactory();
		SSLSocket socket = (SSLSocket) socketFactory.createSocket("127.0.0.1", 5555);
		
		sendDataToServer(socket);
	}
	
	@Test(expected=SSLException.class)
	public void TLS1WayHandshakeWithClientSSLContextNonSupportedProtocolVersion_Exception() throws UnrecoverableKeyException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		SSLContext context = config.getClientSSLContext(KEY_STORE, pass, "TLSv1.1");
		
		SSLSocketFactory socketFactory = context.getSocketFactory();
		SSLSocket socket = (SSLSocket) socketFactory.createSocket("127.0.0.1", 5555);
		sendDataToServer(socket);
		
	}
	
	@Test(expected=SSLException.class)
	public void TLS1WayHandshakeWithClientSSLContextWeakCipher_Exception() throws UnrecoverableKeyException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		SSLContext context = config.getClientSSLContext(KEY_STORE, pass);
		
		SSLSocketFactory socketFactory = context.getSocketFactory();
		SSLSocket socket = (SSLSocket) socketFactory.createSocket("127.0.0.1", 5555);
		String ciphers[] = {"SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA"};
		socket.setEnabledCipherSuites(ciphers);
		sendDataToServer(socket);
		
	}
	
	private void sendDataToServer(Socket socket) throws IOException, EOFException {
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		String text = "Hello";
		System.out.println("Client: " + text);
		out.writeUTF(text);
		String returned = in.readUTF();
		System.out.println(returned);

		in.close();
		out.close();
		socket.close();
	}

}
