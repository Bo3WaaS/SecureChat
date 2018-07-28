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
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.junit.Before;
import org.junit.Test;

import com.eissa.chat.config.SSLContextConfigurator;

public class TestTwoWayHandshake {
	
	/*
	 * Warning: Usage of hard-coded passwords causes security threat. They have been used in this example
	 * for development purposes only.
	 * 
	 * Reference:
	 * https://www.owasp.org/index.php/Use_of_hard-coded_password
	 * 
	 */
	private static final String KEY_STORE = "client_keystore.jks";
	private static final String TRUST_STORE = "client_truststore.jks";
	private static final char[] pass = "secret".toCharArray();
	private static SSLContextConfigurator config = SSLContextConfigurator.getInstance(); 
	
	@Before
	public void setup(){
		
	}
	
	@Test(expected=Exception.class)
	public void NoTLS_Exception() throws UnknownHostException, IOException{
		Socket socket = new Socket("127.0.0.1", 5556);
		sendDataToServer(socket);
	}
	
	@Test(expected=SSLException.class)
	public void TLS1WayHandshake_Exception() throws UnrecoverableKeyException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		SSLContext context = config.getClientSSLContext(TRUST_STORE, pass);
		SSLSocketFactory socketFactory = context.getSocketFactory();
		SSLSocket socket = (SSLSocket) socketFactory.createSocket("127.0.0.1", 5556);
		
		sendDataToServer(socket);
		
	}
	
	@Test
	public void TLS2WayHandshake_Success() throws UnrecoverableKeyException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		SSLContext context = config.getClientSSLContext(KEY_STORE, pass, TRUST_STORE, pass);
		SSLSocketFactory socketFactory = context.getSocketFactory();
		SSLSocket socket = (SSLSocket) socketFactory.createSocket("127.0.0.1", 5556);
		socket.setNeedClientAuth(true);
		
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
