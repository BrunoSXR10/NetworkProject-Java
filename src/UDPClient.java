import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * La classe UDPClient représente un client UDP qui peut envoyer et recevoir des messages.
 * Le client se connecte à un serveur UDP et envoie des messages en utilisant un pseudo.
 */
public class UDPClient extends Thread {
    private String pseudo;
    private InetAddress serverAddress;
    private int serverPort;
    private DatagramSocket clientSocket;
    private InetAddress clientAddress;
    private int clientPort;

    /**
     * Constructeur de la classe UDPClient.
     * 
     * @param pseudo Le pseudo de l'utilisateur.
     * @param serverAddress L'adresse IP du serveur.
     * @param serverPort Le port du serveur.
     * @param clientAddress L'adresse IP du client.
     * @param clientPort Le port du client.
     */
    public UDPClient(String pseudo, InetAddress serverAddress, int serverPort, InetAddress clientAddress, int clientPort) {
        this.pseudo = pseudo;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    /**
     * Exécute le client UDP. Le client envoie son pseudo au serveur et commence à écouter et à envoyer des messages.
     */
    @Override
    public void run() {
        try {
            // Initialisation de la socket client avec l'adresse et le port spécifiés
            clientSocket = new DatagramSocket(clientPort, clientAddress);
            try (Scanner scanner = new Scanner(System.in)) {
				byte[] receiveData = new byte[1024];

				// Thread pour recevoir les messages
				new Thread(() -> {
				    try {
				        while (true) {
				            // Création d'un paquet pour recevoir les données
				            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				            // Réception du paquet
				            clientSocket.receive(receivePacket);
				            // Conversion des données reçues en String
				            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
				            System.out.println(response);
				        }
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
				}).start();

				// Envoi du pseudo au serveur
				sendMessage(pseudo);

				// Boucle pour lire les messages de l'utilisateur et les envoyer au serveur
				while (true) {
				    String msg = scanner.nextLine();
				    if (msg.equalsIgnoreCase("exit")) {
				        sendMessage(pseudo + ": exit");
				        clientSocket.close();
				        System.exit(0);
				    } else {
				        sendMessage(pseudo + ": " + msg);
				    }
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie un message au serveur.
     * 
     * @param message Le message à envoyer.
     */
    private void sendMessage(String message) {
        try {
            // Conversion du message en tableau de bytes
            byte[] sendData = message.getBytes();
            // Création d'un paquet avec les données, l'adresse et le port du serveur
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            // Envoi du paquet via la socket
            clientSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Le point d'entrée principal du programme. Initialise le client UDP avec les arguments fournis.
     * 
     * @param args Les arguments de ligne de commande. Les arguments attendus sont:
     *             pseudo, server-ip, server-port, client-ip, client-port.
     */
    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Usage: java UDPClient <pseudo> <server-ip> <server-port> <client-ip> <client-port>");
            return;
        }

        try {
            String pseudo = args[0];
            InetAddress serverAddress = InetAddress.getByName(args[1]);
            int serverPort = Integer.parseInt(args[2]);
            InetAddress clientAddress = InetAddress.getByName(args[3]);
            int clientPort = Integer.parseInt(args[4]);
            
            if (!UDPScanner.UdpPortScanner(clientPort)) {
                System.out.println("The port " + clientPort + " is not available. Please choose another one.");
                return;
            }
            UDPClient client = new UDPClient(pseudo, serverAddress, serverPort, clientAddress, clientPort);
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
