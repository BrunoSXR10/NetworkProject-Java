import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * La classe UDPScanner fournit des méthodes pour scanner les ports UDP d'un hôte spécifique.
 */
public class UDPScanner {

    /**
     * Scanne une plage de ports UDP sur un hôte spécifié pour déterminer lesquels sont ouverts.
     * 
     * @param host L'adresse de l'hôte à scanner.
     * @param startPort Le port de départ de la plage à scanner.
     * @param endPort Le port de fin de la plage à scanner.
     */
    public static void UdpPortRangeScanner(String host, int startPort, int endPort) {
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

    /**
     * Vérifie si un port UDP spécifique est ouvert sur l'hôte local.
     * 
     * @param port Le numéro du port à vérifier.
     * @return true si le port est ouvert, false sinon.
     */
    public static boolean UdpPortScanner(int port) {
        try {
            DatagramSocket socket = new DatagramSocket(port);
            socket.close();
            return true;
        } catch (SocketException e) {
            return false;
        }
    }
}

