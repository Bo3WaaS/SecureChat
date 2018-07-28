package com.eissa.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import javax.net.ssl.SSLSocket;

public class ServerThread extends Thread{
	
	private long id;
	private SSLSocket socket;
	
	public ServerThread(long id, SSLSocket socket){
		this.id = id;
		this.socket = socket;
	}

	@Override
	public void run() {
		
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			String input = in.readUTF();
			while(!input.equalsIgnoreCase("quit")){
				System.out.println("Client " + id + ": " + input);
				out.writeUTF("Server: " + input);
				try{
					input = in.readUTF();
				} catch (EOFException e) {
					break;
				}
				
			}
			in.close();
			out.close();
			socket.close();	
		} catch (IOException e1) {
			e1.printStackTrace();
		}
			
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public SSLSocket getSocket() {
		return socket;
	}

	public void setSocket(SSLSocket socket) {
		this.socket = socket;
	}

}
