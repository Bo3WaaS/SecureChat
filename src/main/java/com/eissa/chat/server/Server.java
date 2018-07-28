package com.eissa.chat.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import com.eissa.chat.config.SSLContextConfigurator;

public class Server {
	
	/*
	 * Warning: Usage of hard-coded passwords causes security threat. They have been used in this example
	 * for development purposes only.
	 * 
	 * Reference:
	 * https://www.owasp.org/index.php/Use_of_hard-coded_password
	 * 
	 */
	private static final String KEY_STORE = "server-keystore.jks";
	private static final char[] pass = "secret".toCharArray();
	private static SSLContextConfigurator config = SSLContextConfigurator.getInstance();
	private static int counter = 1;
	private static ArrayList<SSLSocket> sockets = new ArrayList<SSLSocket>();
	
	private static String[] enabledProtocols = {"TLSv1.2"};
	// This is the only strong RSA ciphers supported by the library
	private static String [] enabledCiphers = {"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"};
	
	public static void main(String[] args) {
		try {
			SSLContext context = config.getServerSSLContext(KEY_STORE, pass);
			SSLServerSocketFactory socketFactory = context.getServerSocketFactory();
			SSLServerSocket socket = (SSLServerSocket) socketFactory.createServerSocket(5555);
			socket.setEnabledProtocols(enabledProtocols);
			socket.setEnabledCipherSuites(enabledCiphers);
			
			System.out.println("Server Started");
			
			System.out.println("--Server TLs Details--");
			
			System.out.println("Server Enabled Protocols: " + Arrays.toString(socket.getEnabledProtocols()));
			System.out.println("Server Enabled Ciphers: " + Arrays.toString(socket.getEnabledCipherSuites()));
			System.out.println("Client Auth: " + socket.getNeedClientAuth());
			
			while(true){
				
				try{
				SSLSocket clientSocket = (SSLSocket) socket.accept();
				
				System.out.println("\n--Client TLS Details--");
				
				SSLSession session = clientSocket.getSession();
				
				System.out.println("Client Id: " + counter+1);
				System.out.println("Sessions Creation Time: " + getTime(session.getCreationTime()));
				System.out.println("Protocol: " + session.getProtocol());
				System.out.println("Cipher Suite: " + session.getCipherSuite());
				System.out.println("Server Certificate: " + session.getLocalCertificates()[0]);
				
				sockets.add(clientSocket);
				ServerThread thread = new ServerThread(counter++, clientSocket);
				thread.start();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			}
			
						
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private static String getTime(long time){
		LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
		String text = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		return text;
	}

}
