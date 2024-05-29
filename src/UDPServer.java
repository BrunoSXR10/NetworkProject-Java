import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UDPServer {
    private static AtomicInteger clientCounter = new AtomicInteger(0);
    private static ConcurrentHashMap<String, ClientInfo> clients = new ConcurrentHashMap<>();
    private static DatagramSocket serverSocket;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java UDPServer <server-ip> <server-port>");
            return;
        }

        String serverIp = args[0];
        int serverPort = Integer.parseInt(args[1]);

        // Check if the server port is available before starting the server
        if (!UDPScanner.UdpPortScanner(serverPort)) {
            System.out.println("Le port " + serverPort + " n'est pas disponible. Veuillez choisir un autre port.");
            return;
        }

        try {
            serverSocket = new DatagramSocket(serverPort, InetAddress.getByName(serverIp));
            System.out.println("Serveur démarré sur " + serverIp + ":" + serverPort);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    broadcastMessage("LE SERVEUR EST EN PANNE.");
                    if (serverSocket != null && !serverSocket.isClosed()) {
                        serverSocket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                new Thread(new ClientHandler(clientAddress, clientPort, message)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private InetAddress clientAddress;
        private int clientPort;
        private String message;

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
                    System.out.println("Nouveau participant : " + pseudo + " @ " + clientAddress.getHostAddress() + ":" + clientPort);
                    String response = "BIENVENUE " + pseudo + "!";
                    String broadcastMessageTR = pseudo + " REJOINT LA DISCUSSION";
                    broadcastMessageToRest(broadcastMessageTR, clientAddress, clientPort);
                    sendMessage(response, clientAddress, clientPort);
                }

                // Gestion de la déconnexion
                if (msg.equalsIgnoreCase("exit")) {
                    clients.remove(pseudo);
                    String leaveMessage = pseudo + " QUITTE LE CHAT";
                    broadcastMessage(leaveMessage);
                    System.out.println(leaveMessage);
                } else if (msg.startsWith("sto ")) {
                    String[] partsSto = msg.split(" ", 2);
                    String toPseudo = partsSto[1].trim().toLowerCase(); // Convertir en minuscules
                    String stoMessage = partsSto.length > 1 ? partsSto[1].trim() : "";
                    ClientInfo toClient = clients.get(toPseudo);
                    if (toClient != null) {
                        sendMessage(pseudo + ": " + stoMessage, toClient.getAddress(), toClient.getPort());
                    } else {
                        sendMessage("Client non trouvé.", clientAddress, clientPort);
                    }
                } else if (!msg.isEmpty()) {
                    broadcastMessage(pseudo + ": " + msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        private void sendMessage(String message, InetAddress address, int port) {
            try {
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                serverSocket.send(sendPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void broadcastMessage(String message) {
        for (ClientInfo clientInfo : clients.values()) {
            sendMessage(message, clientInfo.getAddress(), clientInfo.getPort());
        }
    }

    private static void broadcastMessageToRest(String message, InetAddress excludeAddress, int excludePort) {
        for (ClientInfo clientInfo : clients.values()) {
            if (!(clientInfo.getAddress().equals(excludeAddress) && clientInfo.getPort() == excludePort)) {
                sendMessage(message, clientInfo.getAddress(), clientInfo.getPort());
            }
        }
    }

    private static void sendMessage(String message, InetAddress address, int port) {
        try {
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            serverSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ClientInfo {
        private InetAddress address;
        private int port;

        public ClientInfo(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        public InetAddress getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }
    }
}
