package com;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);  // Use "localhost" para testes locais
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner stdIn = new Scanner(System.in);

            System.out.println("Connected to the server. Type messages to send:");

            while (true) {
                String userInput = stdIn.nextLine();
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
                out.println(userInput);  
                String response = in.nextLine();  
                System.out.println("Server response: " + response);
            }

            stdIn.close();
            in.close();
            out.close();
            socket.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
