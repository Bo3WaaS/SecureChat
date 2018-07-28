package com.eissa.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Scanner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.eissa.chat.config.SSLContextConfigurator;

public class Client {

	/*
	 * Warning: Usage of hard-coded passwords causes security threat. They have been used in this example
	 * for development purposes only.
	 * 
	 * Reference:
	 * https://www.owasp.org/index.php/Use_of_hard-coded_password
	 * 
	 */
	private static final String KEY_STORE = "client_truststore.jks";
	private static final char[] pass = "secret".toCharArray();
	private static SSLContextConfigurator config = SSLContextConfigurator.getInstance();

	public static void main(String[] args) {

		try {
			SSLContext context = config.getClientSSLContext(KEY_STORE, pass);
			SSLSocketFactory socketFactory = context.getSocketFactory();
			SSLSocket socket = (SSLSocket) socketFactory.createSocket("127.0.0.1", 5555);

			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			Scanner lineInput = new Scanner(System.in);
			String text = lineInput.nextLine();
			while (!text.equalsIgnoreCase("quit")) {
				System.out.println("Client: " + text);
				out.writeUTF(text);
				String returned = in.readUTF();
				System.out.println(returned);
				text = lineInput.nextLine();
			}
			lineInput.close();
			socket.close();
			in.close();
			out.close();
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

}
