import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient extends Thread {
    private String pseudo;
    private InetAddress serverAddress;
    private int serverPort;
    private DatagramSocket clientSocket;
    private InetAddress clientAddress;
    private int clientPort;

    public UDPClient(String pseudo, InetAddress serverAddress, int serverPort, InetAddress clientAddress, int clientPort) {
        this.pseudo = pseudo;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    @Override
    public void run() {
        try {
            clientSocket = new DatagramSocket(clientPort, clientAddress);
            Scanner scanner = new Scanner(System.in);
            byte[] receiveData = new byte[1024];

            // Thread pour recevoir les messages
            new Thread(() -> {
                try {
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        try {
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            clientSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                System.out.println("Le port " + clientPort + " n'est pas disponible. Veuillez choisir un autre port.");
                return;
            }

            UDPClient client = new UDPClient(pseudo, serverAddress, serverPort, clientAddress, clientPort);
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
