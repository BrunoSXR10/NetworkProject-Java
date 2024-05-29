package com;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;

public class Server {

	public static void main(String[] args) throws SocketException {
		
			try {
				ServerSocket listener = new ServerSocket(1234);
				var pool = Executors.newFixedThreadPool(4);
				while(true) {
	                Socket socket = listener.accept();
	                pool.execute(new Process(socket));
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			


	}

}
