package datagram;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ScannerUDP {

    public static void scannerPortsUDP(int startPort, int endPort) {
        for (int port = startPort; port <= endPort; port++) {
            try (DatagramSocket socket = new DatagramSocket(port)) {
                System.out.println("Port " + port + " is open.");
            } catch (SocketException e) {
                System.out.println("Port " + port + " is closed.");
            }
        }
    }

    public static void main(String[] args) {
        scannerPortsUDP(1000, 3010);
    }
}
