package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 8080;

        // 1. Create socket and connect to server
        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true);
             Scanner consoleScanner = new Scanner(System.in)) {

            System.out.println("Connected to Chat Server. Type 'EXIT' to quit.");
            String userInput;

            // 2. Send and receive loop
            while (true) {
                System.out.print("You: ");
                userInput = consoleScanner.nextLine();
                
                serverWriter.println(userInput);

                // Wait for server response
                String serverResponse = serverReader.readLine();
                System.out.println("Server: " + serverResponse);

                // Ordered closure
                if (userInput.equalsIgnoreCase("EXIT")) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}