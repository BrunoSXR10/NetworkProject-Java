import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPScanner {
//    public static boolean UdpPortScanner(String host, int startPort, int endPort) {
//        for (int port = startPort; port <= endPort; port++) {
//            try {
//                DatagramSocket socket = new DatagramSocket(port);
//                socket.connect(InetAddress.getByName(host), port);
//                socket.close();
//                System.out.println("Le port " + port + " est ouvert");
//                return true;
//            } catch (SocketException e) {
//                System.out.println("Le port " + port + " est fermé");
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            }
//        }
//		return false;
//    }
    
    public static boolean UdpPortScanner(int port) {
            try {
                DatagramSocket socket = new DatagramSocket(port);
                socket.close();
                return true;
            } catch (SocketException e) {
        		return false;
            }
    }

    public static void main(String[] args) {
        String host = "127.0.0.1"; // Localhost for testing
        UdpPortScanner(9876);
    }
}
