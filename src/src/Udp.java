package src;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Udp {
	public static void UdpPortScanner(String host, int startPort, int endPort) {
	    for (int port = startPort; port <= endPort; port++) {
	        try {
	            DatagramSocket socket = new DatagramSocket(port);
	            socket.connect(InetAddress.getByName(host), port);
	            socket.close();
		        System.out.println("Le port " + port + " est ouvert");
	        } catch (SocketException e) {
	            System.out.println("Le port " + port + " est fermé");
	        } catch (UnknownHostException e) {
				e.printStackTrace();
			}
	    }
	}
	 public static void main(String[] args) {
	        String host = "127.0.0.1"; // Localhost for testing 
	        int startPort = 1;
	        int endPort = 2000;
	        UdpPortScanner(host, startPort, endPort);
	    }

}
