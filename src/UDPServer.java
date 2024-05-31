import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * La classe UDPServer représente un serveur UDP qui gère les connexions des clients et la communication entre eux.
 */
public class UDPServer {
    private static ConcurrentHashMap<String, ClientInfo> clients = new ConcurrentHashMap<>();
    private static DatagramSocket serverSocket;

    /**
     * Le point d'entrée principal du programme. Initialise le serveur UDP avec les arguments fournis.
     * 
     * @param args Les arguments de ligne de commande. Les arguments attendus sont:
     *             server-ip, server-port.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java UDPServer <server-ip> <server-port>");
            return;
        }

        String serverIp = args[0];
        int serverPort = Integer.parseInt(args[1]);

        // Check if the server port is available before starting the server
        if (!UDPScanner.UdpPortScanner(serverPort)) {
            System.out.println("The port " + serverPort + " is not available. Please choose another one.");
            return;
        }

        try {
            // Initialisation de la socket serveur avec l'adresse et le port spécifiés
            serverSocket = new DatagramSocket(serverPort, InetAddress.getByName(serverIp));
            System.out.println("Server started on " + serverIp + ":" + serverPort);

            // Ajout d'un hook pour gérer la fermeture du serveur proprement
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    broadcastMessage("Server is down");
                    if (serverSocket != null && !serverSocket.isClosed()) {
                        serverSocket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

            byte[] receiveData = new byte[1024];

            while (true) {
                // Création d'un paquet pour recevoir les données des clients
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                // Réception du paquet
                serverSocket.receive(receivePacket);
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                // Traitement du message reçu dans un thread séparé
                new Thread(new ClientHandler(clientAddress, clientPort, message)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Classe interne pour gérer les clients et traiter leurs messages.
     */
    static class ClientHandler implements Runnable {
        private InetAddress clientAddress;
        private int clientPort;
        private String message;

        /**
         * Constructeur de la classe ClientHandler.
         * 
         * @param clientAddress L'adresse IP du client.
         * @param clientPort Le port du client.
         * @param message Le message reçu du client.
         */
        public ClientHandler(InetAddress clientAddress, int clientPort, String message) {
            this.clientAddress = clientAddress;
            this.clientPort = clientPort;
            this.message = message;
        }

        @Override
        public void run() {
            try {
                String[] parts = message.split(":", 2);
                String pseudo = parts[0].trim().toLowerCase(); // Convertir en minuscules
                String msg = parts.length > 1 ? parts[1].trim() : "";

                // Enregistrement du client
                if (!clients.containsKey(pseudo)) {
                    clients.put(pseudo, new ClientInfo(clientAddress, clientPort));
                    System.out.println("New participant : " + pseudo + " @ " + clientAddress.getHostAddress() + ":" + clientPort);
                    String response = "Welcome " + pseudo + "!";
                    String broadcastMessageTR = pseudo + " joined the discussion";
                    broadcastMessageToRest(broadcastMessageTR, clientAddress, clientPort);
                    UDPServer.sendMessage(response, clientAddress, clientPort); // Utilisation de la méthode statique
                }
                else {
                	
                }

                // Gestion de la déconnexion
                if (msg.equalsIgnoreCase("exit")) {
                    clients.remove(pseudo);
                    String leaveMessage = pseudo + " left the chat";
                    broadcastMessage(leaveMessage);
                    System.out.println(leaveMessage);
                //Envoi d'un message privé
                } else if (msg.startsWith("sto ")) {
                    String[] partsSto = msg.split(" ", 3);
                    String toPseudo = partsSto[1].trim().toLowerCase(); // Convertir en minuscules
                    System.out.println(toPseudo);
                    String stoMessage = partsSto.length > 2 ? partsSto[2].trim() : "";
                    ClientInfo toClient = clients.get(toPseudo);
                    if (toClient != null) {
                        UDPServer.sendMessage(pseudo + ": " + stoMessage, toClient.getAddress(), toClient.getPort()); // Utilisation de la méthode statique
                    } else {
                        UDPServer.sendMessage("Client not found", clientAddress, clientPort); // Utilisation de la méthode statique
                    }
                } else if (!msg.isEmpty()) {
                    broadcastMessage(pseudo + ": " + msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        
    }

    /**
     * Envoie un message à tous les clients connectés.
     * 
     * @param message Le message à diffuser.
     */
    private static void broadcastMessage(String message) {
        for (ClientInfo clientInfo : clients.values()) {
            sendMessage(message, clientInfo.getAddress(), clientInfo.getPort());
        }
    }

    /**
     * Envoie un message à tous les clients connectés sauf celui spécifié.
     * 
     * @param message Le message à diffuser.
     * @param excludeAddress L'adresse IP du client à exclure.
     * @param excludePort Le port du client à exclure.
     */
    private static void broadcastMessageToRest(String message, InetAddress excludeAddress, int excludePort) {
        for (ClientInfo clientInfo : clients.values()) {
            if (!(clientInfo.getAddress().equals(excludeAddress) && clientInfo.getPort() == excludePort)) {
                sendMessage(message, clientInfo.getAddress(), clientInfo.getPort());
            }
        }
    }

    /**
     * Envoie un message à un client spécifique.
     * 
     * @param message Le message à envoyer.
     * @param address L'adresse IP du client.
     * @param port Le port du client.
     */
    private static void sendMessage(String message, InetAddress address, int port) {
        try {
            // Conversion du message en tableau de bytes
            byte[] sendData = message.getBytes();
            // Création d'un paquet avec les données, l'adresse et le port du client
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            // Envoi du paquet via la socket
            serverSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * La classe ClientInfo stocke les informations d'un client connecté.
     */
    static class ClientInfo {
        private InetAddress address;
        private int port;

        /**
         * Constructeur de la classe ClientInfo.
         * 
         * @param address L'adresse IP du client.
         * @param port Le port du client.
         */
        public ClientInfo(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        /**
         * Obtient l'adresse IP du client.
         * 
         * @return L'adresse IP du client.
         */
        public InetAddress getAddress() {
            return address;
        }

        /**
         * Obtient le port du client.
         * 
         * @return Le port du client.
         */
        public int getPort() {
            return port;
        }
    }
}
