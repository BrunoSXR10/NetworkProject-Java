package com;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Process implements Runnable {
    private Socket socket;

    public Process(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Loop para continuar a comunicação até que o cliente feche a conexão
            while (in.hasNextLine()) {
                String message = in.nextLine();
                System.out.println("Received: " + message);
                out.println("Echo: " + message);  // Envia a resposta ao cliente
            }

            // Fecha os recursos
            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
